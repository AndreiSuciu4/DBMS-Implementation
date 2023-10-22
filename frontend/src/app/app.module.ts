import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { DbCommandComponent } from './components/db-command/db-command.component';
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from "primeng/button";
import {FormsModule} from "@angular/forms";
import { InputTextareaModule } from 'primeng/inputtextarea';
import {HttpClientModule} from "@angular/common/http";
import {MessagesModule} from "primeng/messages";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    DbCommandComponent
  ],
  imports: [
    BrowserModule,
    InputTextModule,
    ButtonModule,
    FormsModule,
    HttpClientModule,
    InputTextareaModule,
    MessagesModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
