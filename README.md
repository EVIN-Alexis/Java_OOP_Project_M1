# Maltown — Jeu de gestion d'une microbrasserie

Projet final de programmation orientée objet en Java (M1).

## Sommaire

1. [Objectif de l'application](#1-objectif-de-lapplication)
2. [Lancer l'application](#2-lancer-lapplication)
3. [Fonctionnalités principales](#3-fonctionnalités-principales)
4. [Classes principales](#4-classes-principales)
5. [Décisions techniques](#5-décisions-techniques)
6. [Héritage](#6-héritage)
7. [Polymorphisme](#7-polymorphisme)
8. [Classe abstraite et interface](#8-classe-abstraite-et-interface)
9. [Exceptions personnalisées](#9-exceptions-personnalisées)
10. [Chargement des données](#10-chargement-des-données)
11. [Principes SOLID](#11-principes-solid)
12. [Lancer les tests](#12-lancer-les-tests)
13. [Limites connues](#13-limites-connues)

---

## 1. Objectif de l'application

Maltown est un jeu solo local de gestion d'une microbrasserie indépendante. Le joueur achète des matières premières, lance des brassins, attend la fin de la fermentation, embouteille, puis vend sa bière à des bars et à des particuliers. Il doit équilibrer **trésorerie**, **stocks** et **réputation**, et peut investir dans des améliorations ou déposer le bilan.

Le jeu fonctionne au tour par tour : le temps n'avance que lorsque le joueur clique sur « Terminer la journée ».

## 2. Lancer l'application

**Prérequis** : Java 17 ou supérieur, Maven 3.x (ou IntelliJ IDEA). Aucune bibliothèque externe pour le jeu : l'interface utilise Swing, inclus dans le JDK. JUnit 5 sert uniquement aux tests.

**Depuis IntelliJ IDEA** : ouvrir le dossier du projet, puis lancer la classe `brasserie.app.Main`.

## 3. Fonctionnalités principales

- **Production** : 4 styles de bière (Blonde, IPA, Rousse, Brune), 2 cuves de 100 L, brassins de 60 L, fermentation de 3 jours.
- **Achats** : 5 ingrédients (malt standard et spécial, houblon standard et premium, levure standard), refus de l'achat si la trésorerie est insuffisante.
- **Embouteillage** : manuel, environ 172 bouteilles par brassin (5 % de pertes, 2 % avec l'amélioration).
- **Commandes clients** : 1 à 3 nouvelles offres par jour, 4 en attente au maximum, date limite de 2 à 3 jours. Une commande ne peut être acceptée que si le stock suffit.
- **Réputation** (0 à 100) : bonus pour les ventes et les commandes professionnelles, **−1 si une commande est refusée, −2 si elle expire**.
- **Finances** : historique des revenus et dépenses, bénéfice estimé, frais hebdomadaires de 60 € tous les 7 jours.
- **Améliorations** : 3ᵉ cuve (2 000 €), embouteillage amélioré (1 500 €), contrôle qualité (1 000 €).
- **Faillite** : le joueur peut déposer le bilan ; l'entreprise reste consultable en lecture seule.
- **Sauvegardes** : plusieurs parties indépendantes, au format JSON.

Capital initial : 1 500 €. Réputation initiale : 25.

## 4. Classes principales

| Package | Classe | Rôle |
|---------|--------|------|
| `app` | `Main` | Point d'entrée : crée l'entreprise et ouvre la fenêtre |
| `model` | `Company` | État de l'entreprise : argent, jour, réputation, ingrédients, cuves, brassins, stock de bouteilles, commandes |
| `model` | `Ingredient`, `Recipe` | Matières premières et recettes des 4 styles |
| `model` | `BrewBatch`, `Vat` | Un brassin et la cuve qui l'accueille |
| `model` | `Customer` (+ 2 sous-classes), `Order` | Clients et commandes (statuts : en attente, acceptée, refusée, expirée) |
| `model` | `FinanceRecord` | Une ligne de l'historique financier |
| `service` | `ProductionService`, `BottlingService`, `PurchaseService` | Lancer un brassin, embouteiller, acheter |
| `service` | `SalesService`, `OrderService`, `CustomerService` | Ventes, génération et traitement des commandes, portefeuille de clients |
| `service` | `FinanceService`, `DayService` | Revenus/dépenses/bénéfice ; passage d'une journée à l'autre |
| `service` | `SaveService` | Écriture et lecture des sauvegardes JSON |
| `ui` | `MainWindow`, `CraftTheme` | Interface Swing (8 onglets) et thème visuel |

Arborescence :

```
src/
├── main/java/brasserie/{app, model, service, ui}
└── test/java/brasserie/{model, service}
saves/        sauvegardes JSON
```

## 5. Décisions techniques

**1. Règles métier dans des services, pas dans l'interface.**
Les calculs (coût, production, vente, finances, passage du jour) sont dans `service/`. `MainWindow` ne gère que l'affichage et les clics. Pourquoi : les règles sont testables sans lancer l'interface, et on peut changer l'interface sans toucher aux règles.

**2. Un service financier partagé, transmis par le constructeur.**
Un seul `FinanceService` est créé puis donné à `DayService`, `PurchaseService` et `SalesService`. Pourquoi : tous écrivent dans le même historique, sans variable globale ni singleton, et un test peut fournir son propre `FinanceService`.

**3. Sauvegardes JSON écrites à la main, un fichier par partie.**
`SaveService` écrit et lit le JSON sans bibliothèque. Pourquoi : cela évite une dépendance et garde le projet simple à compiler. Contrepartie : le format est lu ligne par ligne, donc il ne faut pas réorganiser les fichiers à la main. Un fichier par entreprise garantit que charger une partie n'en modifie pas une autre.

**4. Temps au tour par tour.**
Rien ne se passe en temps réel : tout est déclenché par « Terminer la journée » (`DayService.advanceDay`). Pourquoi : le comportement est déterministe et facile à tester (on avance le jour d'un pas précis).

**5. Une cuve n'est occupée que pendant la fermentation.**
Elle est libérée à la fin de la fermentation ou à l'embouteillage. Au chargement d'une sauvegarde, la même règle est appliquée : un brassin déjà embouteillé ou terminé ne bloque pas sa cuve.

## 6. Héritage

`Customer` (classe abstraite) est la classe mère de :

- `RegularCustomer` : un particulier, il paie le prix cible ;
- `ProfessionalCustomer` : un bar ou une association, avec une remise de 10 %.

Les deux types de clients partagent un nom, des styles préférés, une plage de quantités et un prix cible (définis une seule fois dans `Customer`) et ne diffèrent que par leur façon de calculer le prix proposé.

## 7. Polymorphisme

Les clients sont manipulés par leur type parent : `CustomerService` expose une `List<Customer>`, chaque `Order` référence un `Customer`, et `SaveService` appelle `customer.getCustomerType()` sans connaître la sous-classe. Le comportement dépend du type réel de l'objet :

```java
Customer c = new ProfessionalCustomer("Bar", List.of("IPA"), 20, 80, 2.00f);
c.calculateOfferedPrice(0);   // 1,80 € (remise de 10 %), alors qu'un RegularCustomer donnerait 2,00 €
```

Voir `CustomerPolymorphismTest` pour les cas testés.

## 8. Classe abstraite et interface

- **`Customer` (classe abstraite)** : elle porte les données et le code communs à tous les clients, et impose aux sous-classes deux méthodes, `calculateOfferedPrice(float)` et `getCustomerType()`. On ne crée jamais un « client » générique : il est forcément particulier ou professionnel, d'où la classe abstraite.
- **`Discountable` (interface)** : elle déclare `applyDiscount(float price)`. Seuls les clients professionnels l'implémentent. Une interface convient ici parce que « pouvoir recevoir une remise » est une capacité, pas un type de client : un autre type de client pourrait l'implémenter sans lien d'héritage avec les autres.

## 9. Exceptions personnalisées

**`InsufficientStockException`** (hérite de `RuntimeException`), dans `model/`. Elle est levée par `SalesService.sellBottles` quand on demande plus de bouteilles que le stock disponible, avec un message indiquant le style, la quantité disponible et la quantité demandée. `acceptOrder` (dans `SalesService` et `OrderService`) l'attrape et refuse la commande sans modifier le stock ni l'argent.

Les autres erreurs métier (fonds insuffisants, ingrédients manquants, cuves occupées, entreprise en faillite, lot pas prêt) sont refusées par validation avant l'action, avec un message clair. Les erreurs de lecture et d'écriture des fichiers sont gérées avec `try / catch` dans `SaveService` : une sauvegarde invalide ou illisible est signalée à l'utilisateur sans corrompre les autres.

## 10. Chargement des données

- Au démarrage, `Main` crée une entreprise par défaut (« Ma Brasserie ») : les ingrédients, les recettes et les clients sont définis dans le code (`Company`, `CustomerService`).
- Les parties enregistrées sont chargées depuis `saves/*.json` avec `SaveService.loadCompany` (onglet Sauvegardes) : on reconstruit l'entreprise (argent, jour, réputation, ingrédients, brassins et cuves, stock, commandes, améliorations) puis l'historique financier avec `loadFinanceRecords`.
- Un fichier invalide renvoie `null` et l'interface affiche « Impossible de charger cette sauvegarde » sans toucher à la partie en cours.
- Les anciennes sauvegardes restent compatibles : un ingrédient qui n'existe plus dans le jeu est ignoré au chargement.

Les sauvegardes sont écrites :

- manuellement, avec le bouton « Sauvegarder » ;
- automatiquement à la création d'une nouvelle partie ;
- automatiquement au dépôt de bilan.

Il n'y a pas de sauvegarde à chaque fin de journée ni à la fermeture : pensez à sauvegarder avant de quitter.

## 11. Principes SOLID

- **S — Responsabilité unique** : chaque service a un rôle précis (`ProductionService` produit, `BottlingService` embouteille, `FinanceService` tient les comptes, `SaveService` sérialise). Les classes du modèle ne contiennent pas de règles de sauvegarde.
- **O — Ouvert/fermé** : ajouter un nouveau type de client revient à créer une sous-classe de `Customer`, sans modifier la classe mère.
- **L — Substitution de Liskov** : `RegularCustomer` et `ProfessionalCustomer` peuvent remplacer `Customer` partout (listes, commandes, sauvegarde) sans casser le code qui les utilise.
- **I — Ségrégation des interfaces** : `Discountable` ne contient qu'une seule méthode et n'est imposée qu'à la classe qui en a besoin.
- **D — Inversion des dépendances** : appliquée partiellement. Les services reçoivent leurs dépendances par leur constructeur (par exemple `FinanceService`), mais ils dépendent de classes concrètes et non d'interfaces.

## 12. Lancer les tests

```bash
mvn test
```

Les tests sont écrits avec JUnit 5 (`src/test/java/brasserie/`) et couvrent les règles métier : production, embouteillage, ventes, commandes, finances, passage des jours, sauvegarde/chargement, et le polymorphisme des clients. Ils vérifient les cas normaux, les cas limites et les cas invalides (stock insuffisant, entreprise en faillite, achat impossible).

## 13. Limites du projet

- Pas de contre-proposition de prix ni de prix conseillé (fonctionnalités secondaires).
- `MainWindow` est une classe volumineuse : elle regroupe les 8 onglets de l'interface.
- Les sauvegardes utilisent un format JSON simple lu ligne par ligne.
- Polymorphisme sous-exploité
