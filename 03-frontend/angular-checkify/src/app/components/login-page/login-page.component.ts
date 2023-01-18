import {Component, OnInit} from '@angular/core';
import {UserService} from 'src/app/services/user.service';
import {User} from 'src/app/common/user';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  user: User = new User();
  errorMessage: string;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit() {
    if (this.userService.currentUserValue) {
      this.router.navigate(['/product-list']);
      return;
    }
  }

  login() {
    this.userService.login(this.user).subscribe(data => {
      this.router.navigate(['/product-list']);
    }, err => {
      this.errorMessage = "Username or password is incorrect";
    });
  }

}
