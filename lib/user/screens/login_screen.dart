import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'register_screen.dart';
import 'forgot_password_screen.dart';
import 'home_screen.dart';
import '../services/auth_service.dart';
import '../services/email_service.dart';
import '../utils/email_validator.dart';
import '../utils/login_attempt_manager.dart';
import '../widgets/password_strength_indicator.dart';
import '../widgets/simple_captcha.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> with TickerProviderStateMixin {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;
  bool _rememberMe = false;
  bool _isLoading = false;
  
  // Animations multiples
  late AnimationController _mainAnimationController;
  late AnimationController _logoAnimationController;
  late AnimationController _fieldAnimationController;
  late AnimationController _buttonAnimationController;
  
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;
  late Animation<double> _logoScaleAnimation;
  late Animation<double> _logoRotationAnimation;
  late Animation<double> _fieldFadeAnimation;
  late Animation<Offset> _fieldSlideAnimation;
  late Animation<double> _buttonScaleAnimation;
  
  // Validation en temps réel
  String? _emailError;
  String? _passwordError;
  
  // Gestion des tentatives et CAPTCHA
  final LoginAttemptManager _attemptManager = LoginAttemptManager();
  bool _showCaptcha = false;
  bool _captchaVerified = false;
  int _loginAttempts = 0;
  
  // État de verrouillage
  bool _isAccountLocked = false;
  Duration _remainingLockTime = Duration.zero;

  @override
  void initState() {
    super.initState();
    
    // Animation principale pour le conteneur
    _mainAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    );
    
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _mainAnimationController, curve: Curves.easeIn),
    );
    
    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.2),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: _mainAnimationController, curve: Curves.easeOutCubic));
    
    // Animation pour le logo
    _logoAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    );
    
    _logoScaleAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _logoAnimationController, curve: Curves.elasticOut),
    );
    
    _logoRotationAnimation = Tween<double>(begin: -0.1, end: 0.0).animate(
      CurvedAnimation(parent: _logoAnimationController, curve: Curves.easeOut),
    );
    
    // Animation pour les champs
    _fieldAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );
    
    _fieldFadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _fieldAnimationController, curve: Curves.easeIn),
    );
    
    _fieldSlideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.3),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: _fieldAnimationController, curve: Curves.easeOutCubic));
    
    // Animation pour le bouton
    _buttonAnimationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    );
    
    _buttonScaleAnimation = Tween<double>(begin: 0.8, end: 1.0).animate(
      CurvedAnimation(parent: _buttonAnimationController, curve: Curves.elasticOut),
    );
    
    // Lancer les animations en séquence
    _mainAnimationController.forward();
    Future.delayed(const Duration(milliseconds: 200), () {
      _logoAnimationController.forward();
    });
    Future.delayed(const Duration(milliseconds: 400), () {
      _fieldAnimationController.forward();
    });
    Future.delayed(const Duration(milliseconds: 600), () {
      _buttonAnimationController.forward();
    });
    
    // Listeners pour validation en temps réel
    _emailController.addListener(_validateEmail);
    _passwordController.addListener(_validatePassword);
    
    // Charger "Se souvenir de moi"
    _loadRememberMe();
    
    // Vérifier le verrouillage de compte
    _checkAccountLock();
  }

  Future<void> _loadRememberMe() async {
    final prefs = await SharedPreferences.getInstance();
    final savedEmail = prefs.getString('saved_email');
    final savedPassword = prefs.getString('saved_password');
    
    if (savedEmail != null && savedPassword != null && mounted) {
      setState(() {
        _rememberMe = true;
        _emailController.text = savedEmail;
        _passwordController.text = savedPassword;
      });
    }
  }

  Future<void> _saveRememberMe() async {
    final prefs = await SharedPreferences.getInstance();
    if (_rememberMe) {
      await prefs.setString('saved_email', _emailController.text);
      await prefs.setString('saved_password', _passwordController.text);
    } else {
      await prefs.remove('saved_email');
      await prefs.remove('saved_password');
    }
  }

  void _validateEmail() {
    final email = _emailController.text;
    if (email.isEmpty) {
      setState(() => _emailError = null);
      return;
    }
    
    final error = EmailValidator.validate(email);
    setState(() => _emailError = error);
  }

  void _validatePassword() {
    final password = _passwordController.text;
    if (password.isEmpty) {
      setState(() => _passwordError = null);
      return;
    }
    
    if (password.length < 6) {
      setState(() => _passwordError = 'Le mot de passe doit contenir au moins 6 caractères');
    } else {
      setState(() => _passwordError = null);
    }
  }

  Future<void> _checkAccountLock() async {
    final email = _emailController.text;
    if (email.isEmpty) return;
    
    final isLocked = await _attemptManager.isLocked(email);
    if (isLocked) {
      final remainingTime = await _attemptManager.getRemainingLockTime(email);
      final isPermanent = await _attemptManager.isPermanentlyLocked(email);
      
      if (mounted) {
        setState(() {
          _isAccountLocked = true;
          _remainingLockTime = remainingTime;
        });
        
        if (isPermanent) {
          _showAccountLockedDialog(isPermanent: true);
        } else {
          _showAccountLockedDialog(isPermanent: false, remainingTime: remainingTime);
        }
      }
    }
  }

  void _showAccountLockedDialog({required bool isPermanent, Duration? remainingTime}) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.lock, color: Colors.red),
            SizedBox(width: 8),
            Text('Compte verrouillé'),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              isPermanent
                  ? 'Votre compte a été verrouillé de façon permanente en raison de nombreuses tentatives de connexion échouées.'
                  : 'Votre compte est verrouillé temporairement.',
              style: const TextStyle(fontSize: 14),
            ),
            if (!isPermanent && remainingTime != null) ...[
              const SizedBox(height: 16),
              Text(
                'Temps restant: ${remainingTime.inMinutes} minutes ${remainingTime.inSeconds % 60} secondes',
                style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.red),
              ),
            ],
            const SizedBox(height: 8),
            const Text(
              'Veuillez réessayer plus tard ou utilisez "Mot de passe oublié".',
              style: TextStyle(fontSize: 12, fontStyle: FontStyle.italic),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _emailController.removeListener(_validateEmail);
    _passwordController.removeListener(_validatePassword);
    _emailController.dispose();
    _passwordController.dispose();
    _mainAnimationController.dispose();
    _logoAnimationController.dispose();
    _fieldAnimationController.dispose();
    _buttonAnimationController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    final email = _emailController.text.trim();
    final password = _passwordController.text;

    // Vérifier le verrouillage
    final isLocked = await _attemptManager.isLocked(email);
    if (isLocked) {
      final remainingTime = await _attemptManager.getRemainingLockTime(email);
      _showAccountLockedDialog(
        isPermanent: await _attemptManager.isPermanentlyLocked(email),
        remainingTime: remainingTime,
      );
      return;
    }

    // Vérifier CAPTCHA si nécessaire
    if (_showCaptcha && !_captchaVerified) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Veuillez compléter la vérification CAPTCHA'),
          backgroundColor: Colors.orange,
        ),
      );
      return;
    }

    setState(() {
      _isLoading = true;
    });

    // Simuler l'authentification (remplacer par votre logique)
    await Future.delayed(const Duration(seconds: 1));

    // Simuler une erreur pour tester le système de tentatives
    final loginSuccess = password.length > 8; // Pour test

    if (mounted) {
      setState(() {
        _isLoading = false;
      });

      if (loginSuccess) {
        // Succès
        await _attemptManager.resetAttempts(email);
        await _saveRememberMe();
        
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (context) => const HomeScreen()),
        );
      } else {
        // Échec
        await _attemptManager.incrementAttempts(email);
        _loginAttempts = await _attemptManager.getAttempts(email);
        
        // Afficher CAPTCHA après 2 tentatives
        if (_loginAttempts >= 2 && !_showCaptcha) {
          setState(() {
            _showCaptcha = true;
            _captchaVerified = false;
          });
        }
        
        // Vérifier si verrouillé
        final isLocked = await _attemptManager.isLocked(email);
        if (isLocked) {
          final isPermanent = await _attemptManager.isPermanentlyLocked(email);
          final remainingTime = await _attemptManager.getRemainingLockTime(email);
          
          // Envoyer email de notification
          await EmailService.sendAccountLockedEmail(
            toEmail: email,
            isPermanent: isPermanent,
            lockDuration: remainingTime,
          );
          
          _showAccountLockedDialog(
            isPermanent: isPermanent,
            remainingTime: remainingTime,
          );
          return;
        }

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Email ou mot de passe incorrect. Tentatives restantes: ${3 - _loginAttempts}',
            ),
            backgroundColor: Colors.red,
            duration: const Duration(seconds: 3),
          ),
        );
      }
    }
  }

  Future<void> _handleGoogleSignIn() async {
    setState(() => _isLoading = true);
    final result = await AuthService.signInWithGoogle();
    
    if (mounted) {
      setState(() => _isLoading = false);
      if (result != null) {
        await AuthService.saveUserData(result);
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (context) => const HomeScreen()),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Connexion Google annulée')),
        );
      }
    }
  }

  Future<void> _handleFacebookSignIn() async {
    setState(() => _isLoading = true);
    final result = await AuthService.signInWithFacebook();
    
    if (mounted) {
      setState(() => _isLoading = false);
      if (result != null) {
        await AuthService.saveUserData(result);
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (context) => const HomeScreen()),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Connexion Facebook échouée')),
        );
      }
    }
  }

  Future<void> _handleGitHubSignIn() async {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Connexion GitHub - À implémenter')),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              const Color(0xFF12ABB0).withOpacity(0.08),
              Colors.white,
              const Color(0xFF12ABB0).withOpacity(0.03),
            ],
            stops: const [0.0, 0.5, 1.0],
          ),
        ),
        child: SafeArea(
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: SlideTransition(
              position: _slideAnimation,
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(24.0),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const SizedBox(height: 40),
                      
                      // Logo animé avec décoration
                      _buildAnimatedLogo(),
                      
                      const SizedBox(height: 30),
                      
                      // Titre avec animation
                      _buildAnimatedTitle(),
                      
                      const SizedBox(height: 40),
                      
                      // Champ Email avec validation et animation
                      SlideTransition(
                        position: _fieldSlideAnimation,
                        child: FadeTransition(
                          opacity: _fieldFadeAnimation,
                          child: _buildEmailField(),
                        ),
                      ),
                      
                      const SizedBox(height: 20),
                      
                      // Champ Password avec indicateur de force et animation
                      SlideTransition(
                        position: _fieldSlideAnimation,
                        child: FadeTransition(
                          opacity: _fieldFadeAnimation,
                          child: _buildPasswordField(),
                        ),
                      ),
                      
                      const SizedBox(height: 10),
                      
                      // Row: Se souvenir de moi + Mot de passe oublié
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          _buildRememberMeCheckbox(),
                          _buildForgotPasswordLink(),
                        ],
                      ),
                      
                      const SizedBox(height: 30),
                      
                      // CAPTCHA si nécessaire
                      if (_showCaptcha && !_captchaVerified) ...[
                        SlideTransition(
                          position: _fieldSlideAnimation,
                          child: FadeTransition(
                            opacity: _fieldFadeAnimation,
                            child: SimpleCaptcha(
                              onVerified: (result) {
                                setState(() => _captchaVerified = true);
                              },
                              onError: () {
                                setState(() => _captchaVerified = false);
                              },
                            ),
                          ),
                        ),
                        const SizedBox(height: 20),
                      ],
                      
                      // Bouton Login animé
                      ScaleTransition(
                        scale: _buttonScaleAnimation,
                        child: _buildLoginButton(),
                      ),
                      
                      const SizedBox(height: 30),
                      
                      // Ligne "or continue with" avec animation
                      SlideTransition(
                        position: _fieldSlideAnimation,
                        child: FadeTransition(
                          opacity: _fieldFadeAnimation,
                          child: _buildDivider(),
                        ),
                      ),
                      
                      const SizedBox(height: 30),
                      
                      // Boutons sociaux avec animation
                      SlideTransition(
                        position: _fieldSlideAnimation,
                        child: FadeTransition(
                          opacity: _fieldFadeAnimation,
                          child: _buildSocialButtons(),
                        ),
                      ),
                      
                      const SizedBox(height: 30),
                      
                      // Lien Create Account avec animation
                      SlideTransition(
                        position: _fieldSlideAnimation,
                        child: FadeTransition(
                          opacity: _fieldFadeAnimation,
                          child: _buildCreateAccountLink(),
                        ),
                      ),
                      
                      const SizedBox(height: 20),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildAnimatedLogo() {
    return RotationTransition(
      turns: _logoRotationAnimation,
      child: ScaleTransition(
        scale: _logoScaleAnimation,
        child: Container(
          width: 140,
          height: 140,
          decoration: BoxDecoration(
            gradient: const LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [Color(0xFF12ABB0), Color(0xFF0E8B8F)],
            ),
            shape: BoxShape.circle,
            boxShadow: [
              BoxShadow(
                color: const Color(0xFF12ABB0).withOpacity(0.4),
                blurRadius: 30,
                spreadRadius: 8,
                offset: const Offset(0, 10),
              ),
            ],
          ),
          child: Stack(
            alignment: Alignment.center,
            children: [
              // Effet de brillance animé
              TweenAnimationBuilder<double>(
                duration: const Duration(seconds: 2),
                tween: Tween(begin: 0.0, end: 1.0),
                builder: (context, value, child) {
                  return Container(
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: LinearGradient(
                        begin: Alignment(-1.0 + value * 2, -1.0 + value * 2),
                        end: Alignment(1.0 - value * 2, 1.0 - value * 2),
                        colors: [
                          Colors.white.withOpacity(0.3 * (1 - value)),
                          Colors.transparent,
                          Colors.white.withOpacity(0.3 * value),
                        ],
                      ),
                    ),
                  );
                },
              ),
              const Icon(
                Icons.pets,
                size: 70,
                color: Colors.white,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAnimatedTitle() {
    return Column(
      children: [
        Text(
          'Welcome Back! 🐾',
          style: GoogleFonts.poppins(
            fontSize: 32,
            fontWeight: FontWeight.bold,
            color: Colors.black87,
            letterSpacing: -0.5,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          'Login to continue',
          style: GoogleFonts.poppins(
            fontSize: 16,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }

  Widget _buildEmailField() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: TextFormField(
        controller: _emailController,
        keyboardType: TextInputType.emailAddress,
        textInputAction: TextInputAction.next,
        validator: EmailValidator.validate,
        style: GoogleFonts.poppins(fontSize: 16),
        decoration: InputDecoration(
          labelText: 'Email',
          labelStyle: GoogleFonts.poppins(color: Colors.grey[600]),
          hintText: 'Enter your email',
          hintStyle: GoogleFonts.poppins(color: Colors.grey[400]),
          prefixIcon: Container(
            margin: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  const Color(0xFF12ABB0).withOpacity(0.2),
                  const Color(0xFF12ABB0).withOpacity(0.1),
                ],
              ),
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Icon(Icons.email_outlined, color: Color(0xFF12ABB0)),
          ),
          errorText: _emailError,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: BorderSide(color: Colors.grey[300]!),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: BorderSide(color: Colors.grey[300]!),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: const BorderSide(color: Color(0xFF12ABB0), width: 2.5),
          ),
          errorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: const BorderSide(color: Colors.red, width: 2),
          ),
          focusedErrorBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(20),
            borderSide: const BorderSide(color: Colors.red, width: 2),
          ),
          filled: true,
          fillColor: Colors.white,
        ),
      ),
    );
  }

  Widget _buildPasswordField() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 10,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: TextFormField(
            controller: _passwordController,
            obscureText: _obscurePassword,
            textInputAction: TextInputAction.done,
            onFieldSubmitted: (_) => _handleLogin(),
            style: GoogleFonts.poppins(fontSize: 16),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Le mot de passe est requis';
              }
              if (value.length < 6) {
                return 'Le mot de passe doit contenir au moins 6 caractères';
              }
              return null;
            },
            decoration: InputDecoration(
              labelText: 'Password',
              labelStyle: GoogleFonts.poppins(color: Colors.grey[600]),
              hintText: 'Enter your password',
              hintStyle: GoogleFonts.poppins(color: Colors.grey[400]),
              prefixIcon: Container(
                margin: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      const Color(0xFF12ABB0).withOpacity(0.2),
                      const Color(0xFF12ABB0).withOpacity(0.1),
                    ],
                  ),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Icon(Icons.lock_outlined, color: Color(0xFF12ABB0)),
              ),
              suffixIcon: IconButton(
                icon: Icon(
                  _obscurePassword ? Icons.visibility_outlined : Icons.visibility_off_outlined,
                  color: Colors.grey[600],
                ),
                onPressed: () {
                  setState(() => _obscurePassword = !_obscurePassword);
                },
              ),
              errorText: _passwordError,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(20),
                borderSide: BorderSide(color: Colors.grey[300]!),
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(20),
                borderSide: BorderSide(color: Colors.grey[300]!),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(20),
                borderSide: const BorderSide(color: Color(0xFF12ABB0), width: 2.5),
              ),
              errorBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(20),
                borderSide: const BorderSide(color: Colors.red, width: 2),
              ),
              focusedErrorBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(20),
                borderSide: const BorderSide(color: Colors.red, width: 2),
              ),
              filled: true,
              fillColor: Colors.white,
            ),
          ),
        ),
        if (_passwordController.text.isNotEmpty) ...[
          const SizedBox(height: 8),
          PasswordStrengthIndicator(password: _passwordController.text),
        ],
      ],
    );
  }

  Widget _buildRememberMeCheckbox() {
    return Row(
      children: [
        Checkbox(
          value: _rememberMe,
          onChanged: (value) {
            setState(() => _rememberMe = value ?? false);
          },
          activeColor: const Color(0xFF12ABB0),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(4),
          ),
        ),
        Text(
          'Se souvenir de moi',
          style: GoogleFonts.poppins(
            fontSize: 14,
            color: Colors.grey[700],
          ),
        ),
      ],
    );
  }

  Widget _buildForgotPasswordLink() {
    return TextButton(
      onPressed: () {
        Navigator.of(context).push(
          MaterialPageRoute(builder: (context) => const ForgotPasswordScreen()),
        );
      },
      child: Text(
        'Mot de passe oublié?',
        style: GoogleFonts.poppins(
          color: const Color(0xFF12ABB0),
          fontWeight: FontWeight.w600,
          fontSize: 14,
        ),
      ),
    );
  }

  Widget _buildLoginButton() {
    return Container(
      height: 60,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [Color(0xFF12ABB0), Color(0xFF0E8B8F)],
        ),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF12ABB0).withOpacity(0.4),
            blurRadius: 20,
            offset: const Offset(0, 8),
            spreadRadius: 2,
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: _isLoading || _isAccountLocked ? null : _handleLogin,
          borderRadius: BorderRadius.circular(20),
          child: Container(
            alignment: Alignment.center,
            child: _isLoading
                ? const SizedBox(
                    height: 24,
                    width: 24,
                    child: CircularProgressIndicator(
                      strokeWidth: 2.5,
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                    ),
                  )
                : Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        'Login',
                        style: GoogleFonts.poppins(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          letterSpacing: 0.8,
                          color: Colors.white,
                        ),
                      ),
                      const SizedBox(width: 10),
                      const Icon(
                        Icons.arrow_forward_rounded,
                        color: Colors.white,
                        size: 22,
                      ),
                    ],
                  ),
          ),
        ),
      ),
    );
  }

  Widget _buildDivider() {
    return Row(
      children: [
        Expanded(
          child: Container(
            height: 1,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  Colors.transparent,
                  Colors.grey[300]!,
                  Colors.transparent,
                ],
              ),
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Text(
            'ou continuer avec',
            style: GoogleFonts.poppins(
              color: Colors.grey[600],
              fontSize: 14,
            ),
          ),
        ),
        Expanded(
          child: Container(
            height: 1,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  Colors.transparent,
                  Colors.grey[300]!,
                  Colors.transparent,
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildSocialButtons() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        _buildSocialButton(Icons.g_mobiledata, 'Google', _handleGoogleSignIn),
        const SizedBox(width: 20),
        _buildSocialButton(Icons.facebook, 'Facebook', _handleFacebookSignIn),
        const SizedBox(width: 20),
        _buildSocialButton(Icons.code, 'GitHub', _handleGitHubSignIn),
      ],
    );
  }

  Widget _buildSocialButton(IconData icon, String label, VoidCallback onPressed) {
    return Column(
      children: [
        Container(
          width: 60,
          height: 60,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(15),
            border: Border.all(color: Colors.grey[300]!),
            boxShadow: [
              BoxShadow(
                color: Colors.grey.withOpacity(0.1),
                blurRadius: 10,
                spreadRadius: 2,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Material(
            color: Colors.transparent,
            child: InkWell(
              onTap: onPressed,
              borderRadius: BorderRadius.circular(15),
              child: Icon(icon, color: const Color(0xFF12ABB0)),
            ),
          ),
        ),
        const SizedBox(height: 8),
        Text(
          label,
          style: GoogleFonts.poppins(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }

  Widget _buildCreateAccountLink() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          "Vous n'avez pas de compte? ",
          style: GoogleFonts.poppins(
            color: Colors.grey[600],
          ),
        ),
        TextButton(
          onPressed: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => const RegisterScreen()),
            );
          },
          child: Text(
            'Créer un compte',
            style: GoogleFonts.poppins(
              color: const Color(0xFF12ABB0),
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ],
    );
  }
}
