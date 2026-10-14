import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApiStatus } from './api-status';

describe('ApiStatus', () => {
  let fixture: ComponentFixture<ApiStatus>;
  let backend: HttpTestingController;

  const statusText = () =>
    (fixture.nativeElement as HTMLElement).querySelector('[role="status"]')?.textContent?.trim();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ApiStatus],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    backend = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ApiStatus);
    fixture.detectChanges();
    TestBed.tick();
  });

  afterEach(() => backend.verify());

  it('says it is connecting until the API answers', () => {
    backend.expectOne('/api/health');
    expect(statusText()).toBe('Connecting to the API…');
  });

  it('says connected once /api/health answers', async () => {
    backend.expectOne('/api/health').flush({ status: 'ok', version: 'abc1234' });
    await fixture.whenStable();
    fixture.detectChanges();
    expect(statusText()).toBe('API connected');
  });

  it('explains what happened and what to do when the API cannot be reached', async () => {
    backend.expectOne('/api/health').flush('down', { status: 503, statusText: 'Service Unavailable' });
    await fixture.whenStable();
    fixture.detectChanges();
    expect(statusText()).toContain("Can't reach the API");
    expect(statusText()).toContain('Reload the page');
  });
});
