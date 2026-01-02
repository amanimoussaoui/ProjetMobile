#!/bin/bash

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🔍 Vérification de l'Implémentation Moderne${NC}\n"

# Base path
BASE_PATH="c:\Users\raeda\Desktop\DEVOPS\PetConnect_Event"

# Array of required files
declare -a FILES=(
    "app/src/main/java/com/example/petconnect_event/event/activity/EventListActivity.java"
    "app/src/main/java/com/example/petconnect_event/event/adapter/EventCardModernAdapter.java"
    "app/src/main/java/com/example/petconnect_event/event/util/AnimationManager.java"
    "app/src/main/java/com/example/petconnect_event/event/util/HapticFeedbackManager.java"
    "app/src/main/res/layout/activity_event_list_new.xml"
    "app/src/main/res/layout/item_event_card_modern.xml"
    "app/src/main/res/layout/layout_shimmer_loading.xml"
    "app/src/main/res/anim/slide_in_right.xml"
    "app/src/main/res/anim/slide_out_left.xml"
    "app/src/main/res/anim/slide_in_left.xml"
    "app/src/main/res/anim/slide_out_right.xml"
    "app/src/main/res/anim/fade_in.xml"
    "app/src/main/res/anim/fade_out.xml"
    "app/src/main/res/anim/scale_in.xml"
    "app/src/main/res/anim/bounce_in.xml"
)

MISSING=0
FOUND=0

echo -e "${YELLOW}📋 Fichiers Requis:${NC}\n"

for file in "${FILES[@]}"; do
    FULL_PATH="$BASE_PATH/$file"
    
    if [ -f "$FULL_PATH" ]; then
        echo -e "${GREEN}✅${NC} $file"
        ((FOUND++))
    else
        echo -e "${RED}❌${NC} $file"
        ((MISSING++))
    fi
done

echo -e "\n${YELLOW}📊 Résumé:${NC}"
echo -e "${GREEN}Trouvés: $FOUND${NC}"
echo -e "${RED}Manquants: $MISSING${NC}"

if [ $MISSING -eq 0 ]; then
    echo -e "\n${GREEN}✨ Tous les fichiers sont présents!${NC}"
    echo -e "${GREEN}🚀 L'implémentation est complète et prête au test.${NC}\n"
    exit 0
else
    echo -e "\n${RED}⚠️ Certains fichiers manquent.${NC}"
    echo -e "${YELLOW}Veuillez vérifier les chemins et créer les fichiers manquants.${NC}\n"
    exit 1
fi
