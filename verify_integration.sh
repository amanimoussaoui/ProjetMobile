#!/bin/bash

# ✅ VÉRIFICATION DE L'INTÉGRATION PetConnect
# Ce script vérifie que tous les fichiers sont correctement créés

echo "🔍 Vérification de l'intégration PetConnect..."
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PROJECT_PATH="c:/Users/raeda/AndroidStudioProjects/PetConnect/app/src/main"

# Compteurs
FOUND=0
MISSING=0

# Fonction pour vérifier un fichier
check_file() {
    local file_path="$1"
    local file_desc="$2"
    
    if [ -f "$file_path" ]; then
        echo -e "${GREEN}✅${NC} $file_desc"
        ((FOUND++))
    else
        echo -e "${RED}❌${NC} $file_desc"
        echo "   Attendu: $file_path"
        ((MISSING++))
    fi
}

# Fonction pour vérifier un dossier
check_dir() {
    local dir_path="$1"
    local dir_desc="$2"
    
    if [ -d "$dir_path" ]; then
        echo -e "${GREEN}✅${NC} $dir_desc"
        ((FOUND++))
    else
        echo -e "${RED}❌${NC} $dir_desc"
        echo "   Attendu: $dir_path"
        ((MISSING++))
    fi
}

echo "═══════════════════════════════════════════════════"
echo "📁 VÉRIFICATION DES DOSSIERS"
echo "═══════════════════════════════════════════════════"
echo ""

check_dir "$PROJECT_PATH/java/com/example/petconnect/shared" "📂 Dossier shared/"
check_dir "$PROJECT_PATH/java/com/example/petconnect/shared/models" "📂 Dossier shared/models/"
check_dir "$PROJECT_PATH/java/com/example/petconnect/shared/services" "📂 Dossier shared/services/"
check_dir "$PROJECT_PATH/java/com/example/petconnect/ui/fragments" "📂 Dossier ui/fragments/"
check_dir "$PROJECT_PATH/java/com/example/petconnect/modules/event/adapters" "📂 Dossier modules/event/adapters/"
check_dir "$PROJECT_PATH/java/com/example/petconnect/modules/adoption/adapters" "📂 Dossier modules/adoption/adapters/"
check_dir "$PROJECT_PATH/java/com/example/petconnect/modules/shop/adapters" "📂 Dossier modules/shop/adapters/"

echo ""
echo "═══════════════════════════════════════════════════"
echo "📄 VÉRIFICATION DES FICHIERS JAVA"
echo "═══════════════════════════════════════════════════"
echo ""

# Modèles
check_file "$PROJECT_PATH/java/com/example/petconnect/shared/models/Event.java" "Event.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/shared/models/Pet.java" "Pet.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/shared/models/Product.java" "Product.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/shared/models/User.java" "User.java"

# Services
check_file "$PROJECT_PATH/java/com/example/petconnect/shared/services/FirebaseService.java" "FirebaseService.java"

# Fragments
check_file "$PROJECT_PATH/java/com/example/petconnect/ui/fragments/HomeFragment.java" "HomeFragment.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/ui/fragments/EventsFragment.java" "EventsFragment.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/ui/fragments/AdoptionFragment.java" "AdoptionFragment.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/ui/fragments/ShopFragment.java" "ShopFragment.java"

# Adapters
check_file "$PROJECT_PATH/java/com/example/petconnect/modules/event/adapters/EventAdapter.java" "EventAdapter.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/modules/adoption/adapters/PetAdapter.java" "PetAdapter.java"
check_file "$PROJECT_PATH/java/com/example/petconnect/modules/shop/adapters/ProductAdapter.java" "ProductAdapter.java"

echo ""
echo "═══════════════════════════════════════════════════"
echo "🎨 VÉRIFICATION DES FICHIERS XML"
echo "═══════════════════════════════════════════════════"
echo ""

# Layouts
check_file "$PROJECT_PATH/res/layout/activity_main.xml" "activity_main.xml"
check_file "$PROJECT_PATH/res/layout/fragment_home.xml" "fragment_home.xml"
check_file "$PROJECT_PATH/res/layout/fragment_events.xml" "fragment_events.xml"
check_file "$PROJECT_PATH/res/layout/fragment_adoption.xml" "fragment_adoption.xml"
check_file "$PROJECT_PATH/res/layout/fragment_shop.xml" "fragment_shop.xml"
check_file "$PROJECT_PATH/res/layout/item_event.xml" "item_event.xml"
check_file "$PROJECT_PATH/res/layout/item_pet.xml" "item_pet.xml"
check_file "$PROJECT_PATH/res/layout/item_product.xml" "item_product.xml"

# Menu
check_file "$PROJECT_PATH/res/menu/bottom_nav_menu.xml" "bottom_nav_menu.xml"

# Drawables
check_file "$PROJECT_PATH/res/drawable/ic_home.xml" "ic_home.xml"
check_file "$PROJECT_PATH/res/drawable/ic_event.xml" "ic_event.xml"
check_file "$PROJECT_PATH/res/drawable/ic_pet.xml" "ic_pet.xml"
check_file "$PROJECT_PATH/res/drawable/ic_shop.xml" "ic_shop.xml"
check_file "$PROJECT_PATH/res/drawable/nav_item_color.xml" "nav_item_color.xml"

# Values
check_file "$PROJECT_PATH/res/values/strings.xml" "strings.xml"

echo ""
echo "═══════════════════════════════════════════════════"
echo "⚙️ VÉRIFICATION DES FICHIERS DE CONFIGURATION"
echo "═══════════════════════════════════════════════════"
echo ""

check_file "$PROJECT_PATH/../../../build.gradle.kts" "build.gradle.kts (root)"
check_file "$PROJECT_PATH/../../build.gradle.kts" "build.gradle.kts (app)"
check_file "$PROJECT_PATH/AndroidManifest.xml" "AndroidManifest.xml"

echo ""
echo "═══════════════════════════════════════════════════"
echo "📚 VÉRIFICATION DE LA DOCUMENTATION"
echo "═══════════════════════════════════════════════════"
echo ""

check_file "$PROJECT_PATH/../../../INTEGRATION_COMPLETE.md" "INTEGRATION_COMPLETE.md"
check_file "$PROJECT_PATH/../../../QUICK_START.md" "QUICK_START.md"
check_file "$PROJECT_PATH/../../../VISUALISATION.md" "VISUALISATION.md"
check_file "$PROJECT_PATH/../../../RESUME.md" "RESUME.md"

echo ""
echo "═══════════════════════════════════════════════════"
echo "📊 RÉSUMÉ"
echo "═══════════════════════════════════════════════════"
echo ""
echo -e "Fichiers/Dossiers trouvés: ${GREEN}$FOUND${NC}"
echo -e "Fichiers/Dossiers manquants: ${YELLOW}$MISSING${NC}"
echo ""

if [ $MISSING -eq 0 ]; then
    echo -e "${GREEN}✅ INTÉGRATION COMPLÈTE ET VALIDÉE!${NC}"
    echo ""
    echo "Prochaines étapes:"
    echo "1. Ajouter google-services.json dans app/"
    echo "2. File → Sync Now dans Android Studio"
    echo "3. Créer un émulateur (Tools → Device Manager)"
    echo "4. Run l'app (Bouton Run ▶️)"
    echo ""
    exit 0
else
    echo -e "${RED}⚠️ CERTAINS FICHIERS SONT MANQUANTS!${NC}"
    echo ""
    echo "Vérifiez les chemins ci-dessus et réessayez."
    echo ""
    exit 1
fi
