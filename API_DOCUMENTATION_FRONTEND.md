# 🏥 Documentation des APIs REST - Backend Gestion Hospitalière
> Guide d'intégration complet destiné aux développeurs Frontend (Angular).

---

## 🌐 1. Configuration & Informations Générales

* **Base URL** : `http://localhost:8080`
* **Headers requis** : `Content-Type: application/json`
* **CORS** : Activé pour `http://localhost:4200` et `http://127.0.0.1:4200`
* **Format des dates** : `YYYY-MM-DD` (ex: `2026-10-15`)
* **Format des heures** : `HH:mm` (ex: `14:30`)

### Structure standard des erreurs (HTTP 4xx / 5xx)
En cas d'erreur de validation, de conflit d'agenda ou de ressource manquante, le backend renvoie un JSON standard :
```json
{
  "timestamp": "2026-10-01T23:30:00",
  "status": 400,
  "error": "Conflit d'agenda",
  "message": "Conflit d'agenda : Le médecin sélectionné a déjà une consultation sur ce créneau."
}
```

---

## 🏛 2. EPIC 1 : Référentiel Hospitalier (Jalon 1)

### 📌 Récupérer les départements et spécialités (CARE-001)
Permet d'alimenter les listes déroulantes de la landing page avec les 3 départements et leurs 9 spécialités exactes.

* **Méthode** : `GET`
* **URL** : `/api/v1/referentiel/specialites`
* **Réponse (HTTP 200)** :
```json
[
  {
    "nom": "Services d'Urgences",
    "specialites": [
      "Urgence adulte",
      "Urgence pédiatrique",
      "Urgence traumatologique"
    ]
  },
  {
    "nom": "Services Cardiologiques",
    "specialites": [
      "Cardiologie interventionnelle",
      "Rythmologie cardiaque",
      "Insuffisance cardiaque"
    ]
  },
  {
    "nom": "Services de Chirurgie Générale",
    "specialites": [
      "Chirurgie digestive et viscérale",
      "Chirurgie pariétale",
      "Chirurgie de provenance"
    ]
  }
]
```

---

## 🏥 3. EPIC 2 : Parcours Patient & Souveraineté des Données (Jalon 1)

### 📌 Soumettre une nouvelle demande de rendez-vous (CARE-102, CARE-103, CARE-104)
Permet à un patient d'enregistrer sa demande.
> **Règle métier NSS (CARE-102)** : Le N° SS doit obligatoirement être préfixé ou vérifié à partir du nom et prénom (ex : `nom-prenom-YYYYMMDD` comme `dupont-jean-19951024` ou `dupont-jean-1234567890123`).
> **Sécurité (CARE-104)** : Le N° SS n'est JAMAIS renvoyé dans la réponse.

* **Méthode** : `POST`
* **URL** : `/api/v1/rendez-vous`
* **Corps de la requête (Request Body)** :
```json
{
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1995-10-24",
  "numeroSecuriteSociale": "dupont-jean-19951024",
  "departement": "Services Cardiologiques",
  "specialite": "Cardiologie interventionnelle",
  "dateSouhaitee": "2026-10-15",
  "motif": "Douleurs thoraciques d'effort"
}
```
* **Réponse (HTTP 201 Created)** :
```json
{
  "numeroDossier": "RDV-202610-8A7F1B",
  "statut": "En attente",
  "nom": "Dupont",
  "prenom": "Jean",
  "departement": "Services Cardiologiques",
  "specialite": "Cardiologie interventionnelle",
  "dateSouhaitee": "2026-10-15",
  "dateCreation": "2026-10-01T23:35:10",
  "message": "Votre demande de rendez-vous a été enregistrée avec succès. Conservez précieusement votre numéro de suivi."
}
```

---

### 📌 Suivre l'état d'avancement d'un dossier (CARE-105)
Permet au patient de consulter le statut courant de sa demande grâce à son identifiant unique.

* **Méthode** : `GET`
* **URL** : `/api/v1/rendez-vous/suivi/{numero_suivi}`
  * Exemple : `/api/v1/rendez-vous/suivi/RDV-202610-8A7F1B`
* **Réponse (HTTP 200)** :
```json
{
  "numeroDossier": "RDV-202610-8A7F1B",
  "statut": "Acceptée",
  "nom": "Dupont",
  "prenom": "Jean",
  "departement": "Services Cardiologiques",
  "specialite": "Cardiologie interventionnelle",
  "dateSouhaitee": "2026-10-15",
  "dateCreation": "2026-10-01T23:35:10",
  "medecinNom": "Dr. Pierre Lefebvre",
  "medecinSpecialite": "Cardiologie interventionnelle",
  "dateConsultation": "2026-10-15",
  "heureConsultation": "14:30",
  "motifRefus": null
}
```
*(Si le statut est "Déclinée", le champ `motifRefus` contient la justification de la secrétaire).*

---

## 📊 4. EPIC 3 : Espace Secrétariat & Affectation (Jalons 2 & 3)

### 📌 Consulter et filtrer les demandes (CARE-201, CARE-202)
Tableau de bord de triage avec filtres optionnels combinables.

* **Méthode** : `GET`
* **URL** : `/api/v1/secretariat/demandes`
* **Paramètres de requête optionnels** :
  * `statut` : `EN_ATTENTE`, `ACCEPTEE`, `DECLINEE`
  * `specialite` : ex. `Cardiologie interventionnelle`
  * `date` : `YYYY-MM-DD`
* **Exemple d'appel** :
  `GET /api/v1/secretariat/demandes?statut=EN_ATTENTE&specialite=Cardiologie+interventionnelle`
* **Réponse (HTTP 200)** :
```json
[
  {
    "id": 1,
    "numeroDossier": "RDV-202610-A1B2C3",
    "nom": "Martin",
    "prenom": "Alice",
    "dateNaissance": "1992-05-14",
    "numeroSecuriteSocialeMasque": "XXXXXXXXXXXXX",
    "departement": "Services Cardiologiques",
    "specialite": "Cardiologie interventionnelle",
    "dateSouhaitee": "2026-10-04",
    "motif": "Douleurs thoraciques lors de l'effort",
    "statut": "EN_ATTENTE",
    "statutLibelle": "En attente",
    "motifRefus": null,
    "medecinId": null,
    "medecinMatricule": null,
    "medecinNom": null,
    "medecinPrenom": null,
    "dateConsultation": null,
    "heureConsultation": null,
    "statutPresence": "NON_DEFINI",
    "dateCreation": "2026-10-01T19:38:15",
    "dateMiseAJour": "2026-10-01T19:38:15"
  }
]
```

---

### 📌 Passer une demande au statut "En attente" (CARE-203)
* **Méthode** : `PATCH`
* **URL** : `/api/v1/secretariat/demandes/{id}/analyser`
* **Réponse (HTTP 200)** : Renvoie l'objet demande avec `statut: "EN_ATTENTE"`.

---

### 📌 Décliner / Refuser une demande avec motif obligatoire (CARE-203)
* **Méthode** : `PATCH`
* **URL** : `/api/v1/secretariat/demandes/{id}/refuser`
* **Corps de la requête (Request Body)** :
```json
{
  "motifRefus": "Dossier incomplet : ordonnance manquante ou indisponibilité sur ce créneau"
}
```
* **Réponse (HTTP 200)** : Renvoie l'objet demande avec `statut: "DECLINEE"` et le `motifRefus`.
* **Erreur (HTTP 400)** si le motif est omis ou vide.

---

### 📌 Lister les médecins par spécialité pour la modale d'affectation (CARE-204)
* **Méthode** : `GET`
* **URL** : `/api/v1/secretariat/medecins?specialite={nom_specialite}`
* **Exemple** : `/api/v1/secretariat/medecins?specialite=Cardiologie+interventionnelle`
* **Réponse (HTTP 200)** :
```json
[
  {
    "id": 7,
    "matricule": "MED-007",
    "nom": "Lefebvre",
    "prenom": "Pierre",
    "specialite": "Cardiologie interventionnelle",
    "departement": "Services Cardiologiques",
    "email": "pierre.lefebvre@hopital.fr",
    "telephone": "0140020001"
  },
  {
    "id": 8,
    "matricule": "MED-008",
    "nom": "Girard",
    "prenom": "Camille",
    "specialite": "Cardiologie interventionnelle",
    "departement": "Services Cardiologiques",
    "email": "camille.girard@hopital.fr",
    "telephone": "0140020002"
  }
]
```

---

### 📌 Affecter un médecin et valider le rendez-vous (CARE-205, CARE-206)
Applique l'algorithme anti-conflit d'horaires. Si le médecin a déjà une consultation sur ce créneau, l'opération est refusée avec une **erreur 400**.

* **Méthode** : `POST`
* **URL** : `/api/v1/secretariat/demandes/{id}/affecter`
* **Corps de la requête (Request Body)** :
```json
{
  "medecinId": 7,
  "dateConsultation": "2026-10-15",
  "heureConsultation": "14:30"
}
```
* **Réponse en cas de succès (HTTP 200)** :
Renvoie la demande avec `statut: "ACCEPTEE"`, `dateConsultation: "2026-10-15"` et `heureConsultation: "14:30"`.
* **Réponse en cas de conflit d'agenda (HTTP 400 Bad Request)** :
```json
{
  "timestamp": "2026-10-01T23:38:15",
  "status": 400,
  "error": "Conflit d'agenda",
  "message": "Conflit d'agenda : Le médecin sélectionné a déjà une consultation sur ce créneau."
}
```

---

## 👨‍⚕️ 5. EPIC 4 : Espace Praticien & Planning Médecin (Jalon 4)

### 📌 Authentification / Connexion par matricule (CARE-301)
* **Option A (GET)** : `GET /api/v1/medecin/profil/{matricule}`
* **Option B (POST)** : `POST /api/v1/medecin/auth`
  * Body : `{ "matricule": "MED-007" }`
* **Réponse (HTTP 200)** :
```json
{
  "id": 7,
  "matricule": "MED-007",
  "nom": "Lefebvre",
  "prenom": "Pierre",
  "specialite": "Cardiologie interventionnelle",
  "departement": "Services Cardiologiques",
  "email": "pierre.lefebvre@hopital.fr",
  "telephone": "0140020001"
}
```
* **Erreur (HTTP 404)** si le matricule n'existe pas.

---

### 📌 Consulter "Mon Planning" (CARE-302, CARE-303)
> **Étanchéité stricte** : Ne renvoie QUE les consultations acceptées du médecin connecté (exclut les dossiers des confrères et les statuts 'En attente' ou 'Déclinée').

* **Méthode** : `GET`
* **URL** : `/api/v1/medecin/{matricule}/planning`
* **Paramètre optionnel** : `?date=YYYY-MM-DD` (si omis, renvoie toutes les consultations futures)
* **Exemple** : `GET /api/v1/medecin/MED-007/planning?date=2026-10-15`
* **Réponse (HTTP 200)** (ordonnée chronologiquement par `heureConsultation`) :
```json
[
  {
    "id": 3,
    "numeroDossier": "RDV-202610-G7H8I9",
    "nomPatient": "Dubois",
    "prenomPatient": "Emma",
    "dateNaissancePatient": "1998-03-08",
    "motif": "Contrôle annuel post-opératoire",
    "dateConsultation": "2026-10-15",
    "heureConsultation": "14:30",
    "specialite": "Cardiologie interventionnelle",
    "statut": "Acceptée",
    "statutPresence": "NON_DEFINI",
    "datePointage": null
  }
]
```

---

### 📌 Pointer la présence du patient & Clôturer la prise en charge (CARE-304)
* **Méthode** : `PUT`
* **URL** : `/api/v1/medecin/rendez-vous/{id}/presence`
* **Corps de la requête (Request Body)** :
```json
{
  "presence": "PRESENT"
}
```
*(Valeurs possibles pour `presence` : `"PRESENT"` ou `"ABSENT"`).*
* **Réponse (HTTP 200)** :
```json
{
  "id": 3,
  "numeroDossier": "RDV-202610-G7H8I9",
  "nomPatient": "Dubois",
  "prenomPatient": "Emma",
  "dateNaissancePatient": "1998-03-08",
  "motif": "Contrôle annuel post-opératoire",
  "dateConsultation": "2026-10-15",
  "heureConsultation": "14:30",
  "specialite": "Cardiologie interventionnelle",
  "statut": "Acceptée",
  "statutPresence": "PRESENT",
  "datePointage": "2026-10-15T14:45:00"
}
```

---

## 💾 6. Données de Test Initialisées (Prêtes à l'emploi)

Le backend peuple automatiquement une base de test au démarrage (`DataInitializer`).

### Médecins de démonstration disponibles
| Matricule | Médecin | Département | Spécialité |
| :--- | :--- | :--- | :--- |
| **MED-001** | Dr. Sophie Durand | Services d'Urgences | Urgence adulte |
| **MED-002** | Dr. Alexandre Moreau | Services d'Urgences | Urgence adulte |
| **MED-003** | Dr. Claire Petit | Services d'Urgences | Urgence pédiatrique |
| **MED-005** | Dr. Julien Blanc | Services d'Urgences | Urgence traumatologique |
| **MED-007** | Dr. Pierre Lefebvre | Services Cardiologiques | Cardiologie interventionnelle |
| **MED-009** | Dr. David Mercier | Services Cardiologiques | Rythmologie cardiaque |
| **MED-011** | Dr. Antoine Roux | Services Cardiologiques | Insuffisance cardiaque |
| **MED-013** | Dr. Luc Bonnet | Services de Chirurgie Générale | Chirurgie digestive et viscérale |
| **MED-015** | Dr. Thomas Fontaine | Services de Chirurgie Générale | Chirurgie pariétale |
| **MED-017** | Dr. Guillaume Lambert | Services de Chirurgie Générale | Chirurgie de provenance |

### Dossiers de démonstration pour le suivi patient
* `RDV-202610-A1B2C3` : Demande au statut **En attente**
* `RDV-202610-D4E5F6` : Demande au statut **Déclinée**
* `RDV-202610-G7H8I9` : Demande au statut **Acceptée** (Dr. Pierre Lefebvre, `MED-007`)

---

## 🅰️ 7. Interfaces TypeScript prêtes à l'emploi pour Angular

Vous pouvez copier ces définitions dans vos modèles Angular (ex: `src/app/models/hopital.models.ts`) :

```typescript
// Référentiel
export interface ReferentielDepartement {
  nom: string;
  specialites: string[];
}

// Statuts
export type StatutDemande = 'EN_ATTENTE' | 'ACCEPTEE' | 'DECLINEE';
export type StatutPresence = 'NON_DEFINI' | 'PRESENT' | 'ABSENT';

// Demande Patient (Création)
export interface DemandeRdvRequest {
  nom: string;
  prenom: string;
  dateNaissance: string; // YYYY-MM-DD
  numeroSecuriteSociale: string; // ex: dupont-jean-19951024
  departement: string;
  specialite: string;
  dateSouhaitee: string; // YYYY-MM-DD
  motif?: string;
}

export interface DemandeRdvResponse {
  numeroDossier: string;
  statut: string;
  nom: string;
  prenom: string;
  departement: string;
  specialite: string;
  dateSouhaitee: string;
  dateCreation: string;
  message: string;
}

// Suivi Patient
export interface SuiviDemandeResponse {
  numeroDossier: string;
  statut: string;
  nom: string;
  prenom: string;
  departement: string;
  specialite: string;
  dateSouhaitee: string;
  dateCreation: string;
  motifRefus?: string;
  medecinNom?: string;
  medecinSpecialite?: string;
  dateConsultation?: string;
  heureConsultation?: string;
}

// Secrétariat
export interface DemandeRdvAdmin {
  id: number;
  numeroDossier: string;
  nom: string;
  prenom: string;
  dateNaissance: string;
  numeroSecuriteSocialeMasque: string;
  departement: string;
  specialite: string;
  dateSouhaitee: string;
  motif?: string;
  statut: StatutDemande;
  statutLibelle: string;
  motifRefus?: string;
  medecinId?: number;
  medecinMatricule?: string;
  medecinNom?: string;
  medecinPrenom?: string;
  dateConsultation?: string;
  heureConsultation?: string;
  statutPresence: StatutPresence;
  dateCreation: string;
  dateMiseAJour: string;
}

export interface AffectationMedecinRequest {
  medecinId: number;
  dateConsultation: string; // YYYY-MM-DD
  heureConsultation: string; // HH:mm (ex: '14:30')
}

export interface RefusDemandeRequest {
  motifRefus: string;
}

// Médecin
export interface Medecin {
  id: number;
  matricule: string;
  nom: string;
  prenom: string;
  specialite: string;
  departement: string;
  email?: string;
  telephone?: string;
}

export interface ConsultationPlanning {
  id: number;
  numeroDossier: string;
  nomPatient: string;
  prenomPatient: string;
  dateNaissancePatient: string;
  motif?: string;
  dateConsultation: string;
  heureConsultation: string;
  specialite: string;
  statut: string;
  statutPresence: StatutPresence;
  datePointage?: string;
}

export interface PointagePresenceRequest {
  presence: StatutPresence;
}
```
