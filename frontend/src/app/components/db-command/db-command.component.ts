import { Component } from '@angular/core';
import {RunCommandService} from "../../service/run-command.service";
import {Message} from "primeng/api";

@Component({
  selector: 'app-db-command',
  templateUrl: './db-command.component.html',
  styleUrls: ['./db-command.component.css']
})
export class DbCommandComponent {
  command: string = '';
  message!: Message[];
  showMessage: boolean = false;

  constructor(private runCommandService: RunCommandService) {}

  sendTextToServer() {
    this.showMessage = false;

    this.runCommandService.runCommand(this.command)
      .subscribe(response => {
        this.command = '';
        this.message = [
          { severity: 'success', summary: 'Success'},
        ];
        this.showMessage = true;
        console.log(response)
    }, error => {
        this.command = '';
        this.message = [
          { severity: 'error', summary: 'Error'},
        ];
        console.log(error)
        this.showMessage = true;
      });
  }
}
