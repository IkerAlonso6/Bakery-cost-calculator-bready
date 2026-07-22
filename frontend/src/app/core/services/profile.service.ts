import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UpdateProfileRequest, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/profile`;

  getProfile(): Observable<User> {
    return this.http.get<User>(this.base);
  }

  updateName(displayName: string): Observable<User> {
    const body: UpdateProfileRequest = { displayName };
    return this.http.put<User>(this.base, body);
  }

  uploadPhoto(file: File): Observable<void> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<void>(`${this.base}/photo`, form);
  }

  deletePhoto(): Observable<void> {
    return this.http.delete<void>(`${this.base}/photo`);
  }
}
