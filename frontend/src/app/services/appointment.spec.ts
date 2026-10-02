import { describe, it, expect, beforeEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { appointmentService } from './appointment';

describe('appointmentService', () => {
  let service: appointmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        appointmentService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(appointmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
