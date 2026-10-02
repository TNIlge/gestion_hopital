# 🏥 Gestion de Rendez-vous Hospitaliers

Système de gestion et de prise de rendez-vous hospitalière développé dans le cadre du projet Agile Scrum (ING2 Info EILCO).

---

## 🚀 Démarrage Rapide

### 1. Prérequis
* **Docker & Docker Compose**
* **Java 21 JDK** (ex: OpenJDK 21)
* **Node.js & npm** (pour le frontend Angular)

---

### 2. Démarrage de la Base de Données (PostgreSQL & pgAdmin)

À la racine du projet, lancez :
```bash
docker compose up -d
```

> **⚠️ En cas d'erreur de port 5432 déjà occupé** :  
> Si vous avez un PostgreSQL installé localement sur votre machine, arrêtez-le pour libérer le port 5432 au conteneur Docker :
> ```bash
> sudo systemctl stop postgresql
> ```

#### Paramètres de connexion BDD :
* **Hôte** : `localhost`
* **Port** : `5432`
* **Nom de la base** : `hospital_db`
* **Utilisateur** : `hospital_user`
* **Mot de passe** : `hospital_password`

#### Interface Web pgAdmin 4 :
* **URL** : [http://localhost:5050](http://localhost:5050)
* **Email** : `admin@eilco.com`
* **Mot de passe** : `admin_password`

---

### 3. Démarrage du Backend (Spring Boot)

Le backend est configuré pour **créer automatiquement les tables** et **initialiser les données de test** (18 médecins couvrant les 9 spécialités et des demandes d'exemple) dès son premier lancement.

```bash
cd backend
./mvnw spring-boot:run
```

Le serveur démarre sur [http://localhost:8080](http://localhost:8080).

Pour exécuter la suite de tests automatisés :
```bash
./mvnw test
```

---

### 4. Démarrage du Frontend (Angular)

```bash
cd frontend
npm install
npm start
```
L'application web est accessible sur [http://localhost:4200](http://localhost:4200).

---

## 📚 Documentation des APIs
Consultez le guide complet d'intégration pour les développeurs frontend dans le fichier [`API_DOCUMENTATION_FRONTEND.md`](./API_DOCUMENTATION_FRONTEND.md).
