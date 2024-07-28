import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../../globals';

@Injectable({
  providedIn: 'root'
})
export class ProviderService {

  urlProviders = `${API_URL}/providers`;
  provider: any;

  constructor(private Http: HttpClient) { }

  listProviders() {
    return this.Http.get(this.urlProviders);
  }

  createProvider(myform: any) {
    this.provider = {
      'name': myform.value.providerName,
      'email': myform.value.providerEmail,
      'address': myform.value.providerAdress
    }
    return this.Http.post(this.urlProviders, this.provider);
  }

  updateProvider(myObj: any) {
    return this.Http.put(this.urlProviders + '/' + myObj['id'], myObj);
  }

  deleteProvider(myObj: any) {
    return this.Http.delete(this.urlProviders + '/' + myObj['id'])
  }
  getProvider(id: any) {
    return this.Http.get(this.urlProviders + '/' + id)
  }


}
