import { registerLocaleData } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { USE_MOCK } from './core/api.config';
import { DemandeHttpService } from './core/services/demande-http.service';
import { DemandeMockService } from './core/services/demande-mock.service';
import { DemandeService } from './core/services/demande.service';

registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(),
    { provide: LOCALE_ID, useValue: 'fr-FR' },
    { provide: DemandeService, useClass: USE_MOCK ? DemandeMockService : DemandeHttpService },
  ]
};
