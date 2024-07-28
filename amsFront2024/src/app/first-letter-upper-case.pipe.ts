import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'firstLetterUpperCase',
  standalone: true
})
export class FirstLetterUpperCasePipe implements PipeTransform {

  transform(value:string): string {
    return value[0].toUpperCase()+value.substring(1,value.length);
  }

}
