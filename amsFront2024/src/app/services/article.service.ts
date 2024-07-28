import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../../globals';

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  urlArticles = `${API_URL}/articles`;
  article: any;

  constructor(private Http: HttpClient) { }

  listArticles() {
    return this.Http.get(this.urlArticles);
  }

  createArticle(myform: any) {
    this.article = {
      'label': myform.value.label,
      'price': myform.value.price,
      'picture': myform.value.picture
    }
    return this.Http.post(this.urlArticles+"/"+myform.value.providerId, this.article);
  }
}
