# accescbs

<img src="https://user-images.githubusercontent.com/328244/125405273-d828b980-e3b7-11eb-9a12-685a6ccb2894.png" height="100px" />

La librairie java AccesCBS permet de réaliser des traitements automatisés sur le [Sudoc](https://www.sudoc.fr/) (logiciel CBS). Elle est avant tout destinée aux applications développées par l'Abes pour ses réseaux d'utilisateurs (ex: ITEM, IdRef, Cidemis ...). D'autres usages pourraient être imaginés et pour éviter des surcharges ou des modifications de masse non maîtrisées du Sudoc, l'Abes souhaite connaître et valider toutes les utilisations de cette librairie (nous contacter : https://stp.abes.fr/).

## Développeurs

### Comment générer une nouvelle version d'AccesCBS ?

Pour générer une nouvelle version de la librairie Java AccesCBS, il faut procéder comme ceci :

1) Commiter/pousser les évolutions ou les correctifs sur la branche ``develop``, puis les fusionner sur la branche ``main``

2) Se rendre dans l'onglet des github actions :  
   ![image](https://user-images.githubusercontent.com/328244/191966326-6f26ce9d-fea6-43b5-b61f-c157827d9a97.png)

3) Sélectionner le workflow "Maven Publish" :  
   ![image](https://user-images.githubusercontent.com/328244/191966437-37de0f27-f96c-43a1-8dc2-853b53656b35.png)

4) Cliquer sur "Run workflow", selectionner la branche ``main``, et indiquer le numéro de version souhaité au [format semver](https://semver.org/lang/fr/) X.X.X (vérifier avant que la release n'existe pas déjà!) :  
   ![image](https://user-images.githubusercontent.com/328244/191966781-e4cd307b-77f5-4fbf-8529-9568909d4ce9.png)

5) Exécuter ("Run workflow") la github action et attendre qu'elle se termine.

6) Vérifier que la release est disponible au niveau de Maven Central (à noter que la mise à disposition peut parfois prendre quelques heures) :  
https://search.maven.org/search?q=a:AccesCbs
