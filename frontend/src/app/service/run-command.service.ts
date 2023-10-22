import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class RunCommandService {

  constructor(private http: HttpClient) {}

  runCommand(data: any) {
    const serverUrl = 'http://localhost:8081/command/run-command';
    return this.http.post(serverUrl, data);
  }
}
