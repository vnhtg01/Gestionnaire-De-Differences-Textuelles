# Rapport Projet – Gestionnaire de différences textuelles
## Annexe:
1. Introduction
2. Architecture de l'Application
3. Analyse Détaillée des Composants Principaux
4. Grille de Fonctionnalités Réalisées
5. Conclusion

## I. Introduction:
Notre projet développe une interface interactive destinée à comparer deux
versions d'un texte, facilitant ainsi la fusion des modifications. Cet outil est
particulièrement utile dans les contextes d'écriture collaborative où les modifications
peuvent inclure des ajouts, des suppressions, et des remplacements. L'objectif est
de permettre aux utilisateurs de modifier un texte tout en conservant un historique
des changements et en permettant à d'autres de revoir et de proposer des
modifications. Cela aide à atteindre un consensus sur une version finale du texte qui
répond aux attentes de toutes les parties impliquées.

## II. Architecture de l'Application
L'application est structurée selon le modèle MVC (Modèle-Vue-Contrôleur),
permettant une séparation claire des responsabilités :
  - Modèle (model): Gère les données de l'application.
  - Vue (view): Affiche l'interface utilisateur et présente les données.
  - Contrôleur (control): Agit comme un intermédiaire entre le modèle et la vue, gérant la logique de contrôle.

## III. Analyse Détaillée des Composants Principaux

| Missions | Priorité | Difficultés |
|-----------|------|----------|
| Interfaces utilisateurs  | Moyenne   | Base      |
| Importation des fichiers| Moyenne   | Base       |
| Visualisation des differences  | Elevée  | Moyenne     |
| Gestion des differences| Elevée   | Elevée       |
| Accepations ou modifications des  differences  | Elevée   | Elevée      |
| Sauvegarde et Export des fichiers| Moyenne   | Base       |

#### 1/ Classe TextMergedController (Package control)
  - Fonctionnalités Principales :
    - Gestion de fichiers : Importe les fichiers texte originaux et modifiés via un FileChooser,
permettant à l'utilisateur de sélectionner des fichiers à comparer. Utilise des filtres d'extension pour limiter les fichiers à des formats spécifiques (par exemple, .txt).
    - Comparaison de textes : Utilise la bibliothèque diff_match_patch pour
effectuer des comparaisons de texte et générer des listes de différences
(LinkedList<Diff>). Ces différences sont ensuite affichées dans l'interface
utilisateur pour permettre la révision.
    - Gestion des changements : Permet à l'utilisateur d'accepter ou de rejeter des
changements spécifiques à travers des dialogues interactifs. Des boutons
"Accepter" et "Refuser" sont associés à chaque différence pour une gestion
fine des modifications.
    - Enregistrement et chargement des commentaires : Gère les commentaires
associés aux fichiers et aux modifications, permettant un suivi des
changements.
  - Implémentation Technique :
    - Utilisation de EventHandlers pour gérer les interactions utilisateur, telles que
les clics sur les boutons ou les modifications de texte.
    - Synchronisation avec le modèle pour mettre à jour les textes originaux et
modifiés en réponse aux actions de l'utilisateur.
    - Gestion des erreurs et validation des entrées pour assurer la robustesse de
l'application (par exemple, validation de la longueur du texte).

#### 2/ Classe TextMergedView (Package view)
  - Composants de l'Interface :
    - Zones de texte : Deux zones (TextArea) pour afficher les textes original et
modifié. Ces zones sont non modifiables pour empêcher l'édition accidentelle
par l'utilisateur.
    - Boutons d'action : Boutons pour l'importation des fichiers, la comparaison des
textes, l'acceptation et le rejet de toutes les modifications.
    - Barre de menu : Contient des options pour sauvegarder ou exporter les
fichiers, ainsi que pour la gestion des changements.
  - Implémentation Technique :
    - Mise en place de FileImportAction, une interface fonctionnelle, pour abstraire
le processus d'importation de fichiers, rendant le code plus modulaire et
réutilisable.
    - Utilisation de ScrollPane pour afficher les différences entre les textes,
permettant à l'utilisateur de naviguer facilement à travers les modifications.
    - Intégration de dialogues modaux (DiffDialog) pour la sélection des différences
à accepter ou à rejeter.

#### 3/ Package model: Classe TextModel
  - Responsabilités :
    - La classe TextModel sert à stocker les données de l'application,
spécifiquement les textes original et modifié. Elle agit comme le modèle dans
l'architecture MVC, fournissant les données aux autres composants sans être
concernée par la logique de présentation ou de contrôle.
  - Fonctionnalités :
    - Gestion des Textes : Permet de stocker et de mettre à jour les textes original
et modifié via des méthodes setter. Ces mises à jour peuvent être
déclenchées par des actions de l'utilisateur dans l'interface ou par des
processus automatiques dans le contrôleur.
  - Implémentation Technique :
    - Simplicité et Efficacité : La classe utilise des champs privés pour stocker les
textes et fournit des méthodes publiques (get et set) pour l'accès et la mise à
jour de ces champs, encapsulant ainsi les données de manière sécurisée.
    - Notifications de Changement : Bien que simple dans cet exemple, dans des
applications plus complexes, TextModel pourrait être amélioré pour notifier le
contrôleur ou la vue lors des modifications des données, par exemple en
utilisant des propriétés observables de JavaFX.

#### 4/ Package view: Classes Diverses
  - 4.1. Classe DiffDialog :
    - Responsabilités :
      - Fournir une interface utilisateur pour afficher les différences entre les fichiers
textuels et permettre à l'utilisateur de choisir les modifications à accepter ou à
rejeter.
    - Fonctionnalités :
      - Affichage de Différences : Utilise une liste de différences (LinkedList<Diff>)
pour afficher chaque différence dans un format visuel, permettant des actions
individuelles sur chaque élément.
    - Actions sur les Différences :
      - Offre des options pour accepter ou rejeter des différences spécifiques à
travers des checkboxes, ainsi que des boutons pour accepter ou rejeter
toutes les modifications d'un coup.
    - Implémentation Technique :
      - Interface Dynamique : Construit dynamiquement le contenu de la boîte de
dialogue en fonction des différences fournies, en utilisant des composants
JavaFX comme VBox et HBox pour organiser les éléments de l'interface
utilisateur.

  - 4.2. Classe TextMergedView :
    - Responsabilités :
      - Construire et afficher l'interface graphique de l'application, agissant comme la
vue dans le modèle MVC.
    - Fonctionnalités :
      - Importation et Affichage des Textes : Permet à l'utilisateur d'importer des
textes via des dialogues de fichiers et de les afficher dans des zones de texte
dédiées.
      - Interaction Utilisateur : Fournit des boutons et des menus pour l'exécution de
commandes comme comparer, sauvegarder, exporter, et gérer les
changements.
    - Implémentation Technique :
      - Gestion Événementielle : Utilise des événements JavaFX pour gérer les
interactions, en liant des actions spécifiques aux boutons et aux menus.
      - Layout et Style : Utilise les classes de layout JavaFX pour organiser
visuellement les composants de l'interface utilisateur et applique des styles
pour améliorer l'expérience utilisateur.

  - 4.3. Classe FileImportAction :
    - Responsabilités :
      - Définir une interface fonctionnelle pour l'importation de fichiers dans les zones
de texte.
    - Fonctionnalités :
      - Abstraction de l'Importation : Fournit un moyen abstrait et réutilisable
d'importer des fichiers, facilitant la modification de l'implémentation sans
affecter le reste du code.
    - Implémentation Technique :
      - Interface Fonctionnelle : Utilise l'interface fonctionnelle de Java pour
permettre différentes implémentations de l'importation de fichiers, ce qui rend
les composants plus flexibles et interchangeables.

## IV. Grille de Fonctionnalités Réalisées

| Fonctionnalité  | Réalisée |
|-----------|--------------|
| Importation de fichiers         | OUI   |
| Affichage et édition de texte         | OUI   |
| Comparaison et affichage des différences         | OUI   |
| Gestion des modifications individuelles         | OUI  |
| Enregistrement des modifications acceptées         | OUI   |
| Commentaires sur les modifications         | OUI   |
| Exportation des résultats         | OUI   |
| Les fichiers doivent être sous forme de texte et comporter jusqu’à 10 000 signes         | OUI   |


## V. Conclusion
L'application développée fournit une solution puissante et conviviale pour
comparer et fusionner des fichiers texte. La répartition claire des responsabilités
grâce à l'architecture MVC et l'utilisation efficace de JavaFX enrichit l'expérience
utilisateur et facilite la maintenance du code. Les améliorations futures pourraient
inclure la prise en charge de formats de fichiers plus diversifiés et l'intégration de
fonctionnalités de collaboration en temps réel, ainsi que la simplification du
processus de sélection des modifications mot par mot plutôt que lettre par lettre.
