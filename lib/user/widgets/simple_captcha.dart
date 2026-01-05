import 'dart:math';
import 'package:flutter/material.dart';

class SimpleCaptcha extends StatefulWidget {
  final Function(String) onVerified;
  final Function() onError;

  const SimpleCaptcha({
    super.key,
    required this.onVerified,
    required this.onError,
  });

  @override
  State<SimpleCaptcha> createState() => _SimpleCaptchaState();
}

class _SimpleCaptchaState extends State<SimpleCaptcha> {
  late int _firstNumber;
  late int _secondNumber;
  late int _correctAnswer;
  final TextEditingController _answerController = TextEditingController();
  bool _isVerified = false;

  @override
  void initState() {
    super.initState();
    _generateNewChallenge();
  }

  void _generateNewChallenge() {
    final random = Random();
    _firstNumber = random.nextInt(10) + 1;
    _secondNumber = random.nextInt(10) + 1;
    _correctAnswer = _firstNumber + _secondNumber;
    _answerController.clear();
    _isVerified = false;
    setState(() {});
  }

  void _verifyAnswer() {
    final userAnswer = int.tryParse(_answerController.text.trim());
    if (userAnswer == _correctAnswer) {
      setState(() {
        _isVerified = true;
      });
      widget.onVerified('verified');
    } else {
      widget.onError();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Réponse incorrecte. Réessayez.'),
          backgroundColor: Colors.red,
        ),
      );
      _generateNewChallenge();
    }
  }

  @override
  void dispose() {
    _answerController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(
            children: [
              const Icon(Icons.security, color: Color(0xFF12ABB0)),
              const SizedBox(width: 8),
              Text(
                'Vérification de sécurité',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: Colors.grey[800],
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          if (!_isVerified) ...[
            Text(
              'Résolvez cette opération pour continuer :',
              style: TextStyle(color: Colors.grey[700], fontSize: 14),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Text(
                  '$_firstNumber + $_secondNumber = ?',
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: TextField(
                    controller: _answerController,
                    keyboardType: TextInputType.number,
                    textAlign: TextAlign.center,
                    decoration: InputDecoration(
                      hintText: 'Réponse',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    ),
                    onSubmitted: (_) => _verifyAnswer(),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton(
                  onPressed: _generateNewChallenge,
                  child: const Text('Nouveau défi'),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: _verifyAnswer,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF12ABB0),
                  ),
                  child: const Text('Vérifier'),
                ),
              ],
            ),
          ] else ...[
            Row(
              children: [
                const Icon(Icons.check_circle, color: Colors.green),
                const SizedBox(width: 8),
                const Text(
                  'Vérification réussie',
                  style: TextStyle(
                    color: Colors.green,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ],
        ],
      ),
    );
  }
}





