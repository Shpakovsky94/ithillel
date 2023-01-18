import {Component} from '@angular/core';
import {UserService} from 'src/app/services/user.service';
import {Router} from '@angular/router';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-checkify';

  constructor(private userService: UserService,
              private router: Router) {
  }

  logged(): boolean {
    return this.userService.currentUserValue ? true : false;
  }

  logOut() {
    this.userService.logOut();
  }
}
