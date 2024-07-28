import { Component } from '@angular/core';
import { PostService } from '../services/post.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './post.component.html',
  styleUrl: './post.component.css'
})
export class PostComponent {
  constructor(private service: PostService) {
  }

  posts: any;
  ngOnInit(): void {

    this.service.getPosts().subscribe(
      data => {
        this.posts = data;

      }
    );

  }
}
