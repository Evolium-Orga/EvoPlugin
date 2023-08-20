# EvoDefault

Le plugin BaseTemplate sert de point de départ pour développer des plugins Bukkit en Java. Il fournit une structure de modèle de base et des composants essentiels pour vous aider à démarrer rapidement.

## Pour commencer

Pour utiliser le plugin EvoDefault, suivez ces étapes :

Clonez le dépôt ou téléchargez le code source sur votre machine locale.
Configurez votre environnement de développement avec les dépendances et les bibliothèques requises.
Importez le projet dans votre IDE Java préféré (par exemple, IntelliJ IDEA, Eclipse). Nous recommandons l'utilisation d'IntelliJ IDEA !
Mettez à jour les références du nom du plugin dans les fichiers suivants :
settings.gradle
build.gradle.kts
plugin.yml
Personnalisez le plugin en fonction de vos besoins spécifiques. Apportez des modifications au code fourni ou ajoutez vos propres classes pour étendre les fonctionnalités.
Une fois la configuration terminée, remplacez le contenu de ce fichier par le contenu de "README-plugin.md" et supprimez ce dernier.

## Règles de codage

Lorsque vous travaillezavec EvoDefault, veuillez respecter les règles et les conventions de codage suivantes :

**Nom des packages** : Les packages doivent être nommés en minuscules avec des mots séparés par des points. Par exemple : fr.evolium.evodefault

**Nom des classes** : Les noms de classe doivent suivre la notation PascalCase (UpperCamelCase), en commençant par une lettre majuscule. Par exemple : BaseTemplate, ExampleExecutor, JoinManager.

**Nom des variables** : Les variables doivent utiliser la notation camelCase en commençant par une lettre minuscule. Par exemple : logger, formator, pm.

**Nom des constantes** : Les constantes doivent être en majuscules avec des mots séparés par des underscores. Par exemple : WELCOME_MESSAGE, DEBUG_MODE.

**Indentation et formatage** : Utilisez une indentation cohérente (espaces plutôt que des tabulations) pour améliorer la lisibilité du code. Respectez des règles de formatage cohérentes, telles que placer les accolades ouvrantes sur la même ligne que la déclaration de méthode ou de classe correspondante.

**Commentaires** : Incluez des commentaires pour expliquer le but et la fonctionnalité des classes, des méthodes et des sections de code importantes. Les commentaires doivent être clairs, concis et fournir des informations utiles.

**Gestion des erreurs** : Mettez en place une gestion appropriée des erreurs en utilisant des blocs try-catch ou en propageant les exceptions au niveau approprié.
Utilisation de final et static : Utilisez le mot-clé final pour les variables constantes et le mot-clé static pour les variables et les méthodes de niveau de classe, lorsque cela est approprié.

**Organisation du code** : Regroupez le code connexe ensemble ! Seule la classe principale devrait être à la racine du plugin. Utilisez les modificateurs d'accès appropriés (public, private) pour contrôler la visibilité et l'accessibilité des membres de classe.

**Convention de nommage des méthodes** : Les noms de méthodes doivent utiliser la notation camelCase et être descriptifs de leur objectif. Par exemple : onEnable, onDisable, setCommands, loadConfigFiles.
Veuillez garder ces règles de codage à l'esprit lorsque vous travaillez sur votre plugin pour maintenir la cohérence et la lisibilité du code.

## Problème connu

Si vous rencontrez une erreur lors du chargement du projet Gradle indiquant "Unsupported class file major version 64" et si définir votre JDK de projet sur 17 ne résout pas ce problème, cela signifie probablement que vos paramètres Gradle tentent d'utiliser Java 20. Vous pouvez résoudre ce problème en cliquant sur Gradle -> la petite roue dentée -> Paramètres Gradle et en définissant "Gradle JVM" sur 17.

##Contribution

Si vous rencontrez des problèmes, avez des suggestions ou souhaitez contribuer au projet, n'hésitez pas à ouvrir une demande ou à soumettre une demande de modification. Vos contributions sont grandement appréciées !
