import { TestBed } from '@angular/core/testing';

import { RunCommandService } from './run-command.service';

describe('RunCommandService', () => {
  let service: RunCommandService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RunCommandService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
