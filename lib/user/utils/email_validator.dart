import 'package:email_validator/email_validator.dart' as email_validator;

class EmailValidator {
  static bool isValid(String email) {
    return email_validator.EmailValidator.validate(email);
  }
  
  static String? validate(String? email) {
    if (email == null || email.isEmpty) {
      return 'L\'email est requis';
    }
    if (!isValid(email)) {
      return 'Veuillez entrer un email valide';
    }
    return null;
  }
}



