# TP7 -JAX-RS (Jersey)

Petite API REST de gestion de comptes bancaires, réalisée dans le cadre du TP7. Elle expose les quatre opérations CRUD sur une entité `Compte`, stockée dans une base H2 en mémoire, et sait répondre aussi bien en **JSON** qu'en **XML**.

Le point particulier du TP : on n'utilise pas Spring MVC (`@RestController`) pour écrire les services, mais **JAX-RS avec Jersey** (`@Path`, `@GET`, `@POST`...), intégré à Spring Boot.

## Sommaire

1. [Technologies utilisées](#technologies-utilisées)
2. [Structure du projet](#structure-du-projet)
3. [Lancer le projet](#lancer-le-projet)
4. [Le modèle de données](#le-modèle-de-données)
5. [Les endpoints](#les-endpoints)
6. [Exemples de requêtes](#exemples-de-requêtes)
7. [Résultats obtenus](#résultats-obtenus)

## Technologies utilisées

| Outil | Version | Rôle |
|---|---|---|
| Java | 17 | Langage |
| Spring Boot | 3.3.5 | Socle de l'application |
| Spring Data JPA | - | Accès aux données (`JpaRepository`) |
| Jersey (JAX-RS) | 3.1 | Implémentation des services REST |
| H2 Database | 2.2 | Base de données en mémoire |
| Lombok | - | Génération des getters, setters et constructeurs |
| JAXB | 4.0 | Sérialisation XML |
| Maven | - | Gestion des dépendances |

Jackson (fourni avec Spring Boot) s'occupe du JSON, JAXB du XML.

## Structure du projet

<img width="298" height="431" alt="image" src="https://github.com/user-attachments/assets/f139b1e0-8875-45b5-867f-6f814d319827" />


## Lancer le projet

Il faut un JDK 17 ou plus récent et Maven (celui intégré à IntelliJ suffit).

**Avec IntelliJ IDEA**

1. `File` > `Open`, puis choisir le dossier du projet (celui qui contient `pom.xml`).
2. Attendre que Maven télécharge les dépendances.
3. Lancer la classe `JaxrsjerseyApplication`.

**En ligne de commande**

```bash
mvn spring-boot:run
```

L'application démarre sur le port **8082**. Au démarrage, trois comptes aléatoires sont insérés en base (deux épargne, un courant) et affichés dans la console.

La console H2 est accessible sur `http://localhost:8082/h2-console` avec les paramètres suivants :

| Champ | Valeur |
|---|---|
| JDBC URL | `jdbc:h2:mem:banque` |
| User Name | `root` |
| Password | (vide) |

## Le modèle de données

Un compte est composé de :

| Champ | Type | Remarque |
|---|---|---|
| `id` | `Long` | Clé primaire, générée automatiquement |
| `solde` | `double` | Montant du compte |
| `dateCreation` | `Date` | Stockée sans l'heure (`TemporalType.DATE`) |
| `type` | `TypeCompte` | `COURANT` ou `EPARGNE` |

Le type est enregistré avec `EnumType.ORDINAL`, comme demandé dans l'énoncé : en base on retrouve donc `0` pour `COURANT` et `1` pour `EPARGNE` (voir la capture de la console H2 plus bas). Les clients de l'API, eux, manipulent bien le nom (`"EPARGNE"`).

## Les endpoints

Toutes les routes sont préfixées par `/banque`.

| Méthode | URL | Description | Corps de la requête |
|---|---|---|---|
| GET | `/banque/comptes` | Liste de tous les comptes | - |
| GET | `/banque/comptes/{id}` | Un compte par son identifiant | - |
| POST | `/banque/comptes` | Création d'un compte | JSON ou XML |
| PUT | `/banque/comptes/{id}` | Mise à jour d'un compte existant | JSON ou XML |
| DELETE | `/banque/comptes/{id}` | Suppression d'un compte | - |

Le format de la réponse dépend de l'en-tête `Accept` envoyé par le client (`application/json` ou `application/xml`). Pour les `POST` et `PUT`, le format du corps est indiqué par l'en-tête `Content-Type`.

Quelques comportements à connaître :

- si l'identifiant demandé n'existe pas, `GET` et `PUT` renvoient une réponse vide (code 204), car la méthode retourne `null` ;
- un navigateur annonce `application/xml` dans son en-tête `Accept`, c'est pourquoi l'affichage direct d'une URL donne du XML (première capture ci-dessous) ;


## Résultats obtenus

<img width="1165" height="545" alt="Capture d&#39;écran 2026-10-21 115602" src="https://github.com/user-attachments/assets/496060c0-489d-48d4-85c3-9fc6657a65de" />


### Réponse dans le navigateur

En tapant l'URL directement dans le navigateur, on obtient du XML, puisque le navigateur l'annonce dans son en-tête `Accept`.

<img width="962" height="538" alt="Capture d&#39;écran 2026-10-21 095534" src="https://github.com/user-attachments/assets/93443ce6-a6bb-4ee9-b743-4b31090b2d42" />


### Réponse en JSON

Avec `Accept: application/json`, la même liste est renvoyée au format JSON. On voit bien les trois comptes créés au démarrage par le `CommandLineRunner`.

<img width="811" height="382" alt="Capture d&#39;écran 2026-10-21 111426" src="https://github.com/user-attachments/assets/1bbbb838-56d6-4b39-bad6-a2e399a17cae" />


### Réponse en XML

Avec `Accept: application/xml`, la racine du document est `<comptes>` et chaque compte est un élément `<compte>`. La date est au format ISO 8601.

<img width="638" height="398" alt="Capture d&#39;écran 2026-10-21 111459" src="https://github.com/user-attachments/assets/b063b768-84fa-435b-aaf4-9e96b7fc1ed6" />


### Vérification dans la base H2

Une requête `SELECT * FROM COMPTE` dans la console H2 confirme que les données sont bien en base. La colonne `TYPE` contient `0` ou `1` à cause du choix `EnumType.ORDINAL` : le compte 2 (`0`) est le compte courant, les comptes 1 et 3 (`1`) sont des comptes épargne, ce qui correspond bien aux réponses de l'API.

<img width="608" height="385" alt="Capture d&#39;écran 2026-10-21 111522" src="https://github.com/user-attachments/assets/bdbc909b-9bf0-4d90-885b-a9894016f09d" />


