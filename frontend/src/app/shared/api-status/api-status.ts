import { httpResource } from '@angular/common/http';
import { Component } from '@angular/core';

interface HealthResponse {
  status: string;
  version: string;
}

@Component({
  selector: 'app-api-status',
  templateUrl: './api-status.html',
  styleUrl: './api-status.css',
})
export class ApiStatus {
  // The request starts as soon as the component is created. The resource
  // exposes loading, value and error as signals, so the template reads them
  // directly and no state field is kept by hand.
  protected readonly health = httpResource<HealthResponse>(() => '/api/health');
}
