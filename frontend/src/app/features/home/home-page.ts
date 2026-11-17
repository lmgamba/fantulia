import { Component } from '@angular/core';
import { ApiStatus } from '../../shared/api-status/api-status';

@Component({
  imports: [ApiStatus],
  selector: 'app-home-page',
  styleUrl: './home-page.css',
  templateUrl: './home-page.html',
})
export class HomePage {}
