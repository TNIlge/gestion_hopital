import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface Specialite {
  nom: string;
  icone: string;
  description: string;
  sousSpecialites: string[];
}

@Component({
  selector: 'app-landing',
  imports: [RouterLink],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class Landing {
  protected readonly logoDisponible = signal(true);

  protected readonly annee = new Date().getFullYear();

  protected readonly specialites: Specialite[] = [
    {
      nom: 'Urgences',
      icone: 'bi-truck-front',
      description: 'Une prise en charge rapide, 24h/24, pour adultes et enfants.',
      sousSpecialites: ['Urgence adulte', 'Urgence pédiatrique', 'Traumatologie'],
    },
    {
      nom: 'Cardiologie',
      icone: 'bi-heart-pulse',
      description: 'Diagnostic et suivi des pathologies du cœur par nos spécialistes.',
      sousSpecialites: ['Interventionnelle', 'Rythmologie', 'Insuffisance cardiaque'],
    },
    {
      nom: 'Chirurgie générale',
      icone: 'bi-bandaid',
      description: 'Des interventions encadrées par des chirurgiens expérimentés.',
      sousSpecialites: ['Digestive et viscérale', 'Pariétale', 'Chirurgie de provenance'],
    },
  ];

  protected readonly etapes = [
    {
      icone: 'bi-ui-checks',
      titre: 'Remplissez le formulaire',
      texte: 'Choisissez la spécialité, la langue et la date souhaitée.',
    },
    {
      icone: 'bi-upc-scan',
      titre: 'Notez votre numéro',
      texte: 'Un identifiant unique vous est remis pour suivre votre demande.',
    },
    {
      icone: 'bi-calendar2-check',
      titre: 'Recevez la validation',
      texte: 'Le secrétariat vous assigne un médecin disponible.',
    },
  ];
}
