import 'package:shared_preferences/shared_preferences.dart';

class LoginAttemptManager {
  static const String _prefName = 'login_attempts';
  static const String _keyAttempts = 'attempts_';
  static const String _keyLocked = 'locked_';
  static const String _keyLockTime = 'lock_time_';
  static const String _keyPermanentLock = 'permanent_lock_';
  static const int maxAttempts = 3;
  static const int lockDurationMinutes = 15; // 15 minutes
  
  Future<void> incrementAttempts(String email) async {
    final prefs = await SharedPreferences.getInstance();
    final key = '$_keyAttempts$email';
    final attempts = (prefs.getInt(key) ?? 0) + 1;
    await prefs.setInt(key, attempts);
    
    if (attempts >= maxAttempts) {
      await lockAccount(email, permanent: attempts >= 5);
    }
  }
  
  Future<void> resetAttempts(String email) async {
    final prefs = await SharedPreferences.getInstance();
    final key = '$_keyAttempts$email';
    await prefs.remove(key);
    await prefs.remove('$_keyLocked$email');
    await prefs.remove('$_keyLockTime$email');
    await prefs.remove('$_keyPermanentLock$email');
  }
  
  Future<int> getAttempts(String email) async {
    final prefs = await SharedPreferences.getInstance();
    final key = '$_keyAttempts$email';
    return prefs.getInt(key) ?? 0;
  }
  
  Future<bool> isLocked(String email) async {
    final prefs = await SharedPreferences.getInstance();
    
    // Vérifier si verrouillé de façon permanente
    final permanentLock = prefs.getBool('$_keyPermanentLock$email') ?? false;
    if (permanentLock) return true;
    
    final locked = prefs.getBool('$_keyLocked$email') ?? false;
    if (!locked) return false;
    
    final lockTime = prefs.getInt('$_keyLockTime$email') ?? 0;
    final currentTime = DateTime.now().millisecondsSinceEpoch;
    final elapsedMinutes = (currentTime - lockTime) / (1000 * 60);
    
    if (elapsedMinutes >= lockDurationMinutes) {
      // Le verrouillage a expiré
      await resetAttempts(email);
      return false;
    }
    
    return true;
  }
  
  Future<Duration> getRemainingLockTime(String email) async {
    final prefs = await SharedPreferences.getInstance();
    final lockTime = prefs.getInt('$_keyLockTime$email') ?? 0;
    final currentTime = DateTime.now().millisecondsSinceEpoch;
    final elapsed = currentTime - lockTime;
    final remaining = (lockDurationMinutes * 60 * 1000) - elapsed;
    
    if (remaining <= 0) return Duration.zero;
    return Duration(milliseconds: remaining.toInt());
  }
  
  Future<void> lockAccount(String email, {bool permanent = false}) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('$_keyLocked$email', true);
    await prefs.setInt('$_keyLockTime$email', DateTime.now().millisecondsSinceEpoch);
    if (permanent) {
      await prefs.setBool('$_keyPermanentLock$email', true);
    }
  }
  
  Future<bool> isPermanentlyLocked(String email) async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool('$_keyPermanentLock$email') ?? false;
  }
  
  // Méthode pour déverrouiller un compte (admin seulement)
  Future<void> unlockAccount(String email) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('$_keyLocked$email');
    await prefs.remove('$_keyLockTime$email');
    await prefs.remove('$_keyPermanentLock$email');
    await resetAttempts(email);
  }
}





