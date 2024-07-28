import { Component } from '@angular/core';
import { ProviderService } from '../services/provider.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-provider',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './add-provider.component.html',
  styleUrl: './add-provider.component.css'
})
export class AddProviderComponent {

  provider: any;
  constructor(private service: ProviderService, private router: Router) {
  }
  createProvider(myform: any) {
    this.service.createProvider(myform).subscribe(
      response => {
        //console.log(response);
        // forcer la redirection que si j'obtient la réponse du serveur backend
        this.router.navigate(['listProvider']);
      }
      );
  }
}
