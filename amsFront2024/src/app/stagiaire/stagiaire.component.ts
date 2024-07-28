import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FirstLetterUpperCasePipe } from "../first-letter-upper-case.pipe";
import { UsersService } from '../services/users.service';

@Component({
  selector: 'app-stagiaire',
  standalone: true,
  templateUrl: './stagiaire.component.html',
  styleUrl: './stagiaire.component.css',
  imports: [CommonModule, FirstLetterUpperCasePipe]
})
export class StagiaireComponent {
  formation: string = "Angular";
  formateur: string = "Dr. Mohamed Amine MEZGHICH";
  stagiaires: string[] = ["sofien", "marwa", "hakim", "borel", "fares", "atef"];
  users: any;

  constructor(private service: UsersService) {
    console.log("Constructeur");
  }


  ngOnInit(): void {
    console.log("ngOnInit");
    this.service.getUsers().subscribe(
      data => {
        this.users = data;
        console.log(this.users)
      }
    );

  }

}
