import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProviderService } from '../services/provider.service';
import { ArticleService } from '../services/article.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-article',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './add-article.component.html',
  styleUrl: './add-article.component.css'
})
export class AddArticleComponent {
  article: any;
  providers: any;
  providerId: any;
  constructor(
    private providerService: ProviderService,
    private articleService: ArticleService,
    private router: Router) { }

    ngOnInit(){
      this.providerService.listProviders().subscribe(
        data =>{
           this.providers = data;
           console.log(this.providers);
        }
      );
    }

  createArticle(myform:any) {

    this.articleService.createArticle(myform).subscribe(
      response => {
        //console.log(response);
        // forcer la redirection que si j'obtient la réponse du serveur backend
        this.router.navigate(['listArticle']);
      }
    );
  }
}
