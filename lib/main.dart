import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'user/screens/welcome_screen.dart';

void main() {
  runApp(const PetConnectApp());
}

class PetConnectApp extends StatelessWidget {
  const PetConnectApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PetConnect',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primaryColor: const Color(0xFF12ABB0),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF12ABB0),
          primary: const Color(0xFF12ABB0),
          secondary: Colors.white,
          background: Colors.white,
        ),
        textTheme: GoogleFonts.poppinsTextTheme(),
        useMaterial3: true,
      ),
      home: const WelcomeScreen(),
    );
  }
}



