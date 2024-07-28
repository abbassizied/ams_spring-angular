import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ProviderService } from '../services/provider.service';
import { Router, RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-list-provider',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule],

  templateUrl: './list-provider.component.html',
  styleUrl: './list-provider.component.css'
})
export class ListProviderComponent {
  providers: any;
  constructor(private service: ProviderService, private router: Router) { }

  ngOnInit() {
    this.refreshListProviders();
  }

  deleteProvider(myObj: any) {
    //console.log(this.provider);
    this.service.deleteProvider(myObj).subscribe(response => {
      //console.log(response);
      this.refreshListProviders();
    })
  }

  refreshListProviders() {
    this.service.listProviders().subscribe(
      response => {
        this.providers = response;
      }
    );
  }

  updateProvider(myObj: any) {
    this.router.navigate(['updateProvider' + '/' + myObj['id']]);
  }
}
