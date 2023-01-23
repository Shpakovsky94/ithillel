import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {User} from 'src/app/common/user';
import {Router} from '@angular/router';

let API_URL = "http://localhost:8080/api/user/";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userHeaders: HttpHeaders;


  public currentUser: Observable<User>;
  private currentUserSubject: BehaviorSubject<User>;

  constructor(private http: HttpClient,
              private router: Router) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }


  login(user: User): Observable<any> {
    const headers = new HttpHeaders(user ? {
      authorization: 'Basic ' + btoa(user.username + ':' + user.password)
    } : {});
    this.userHeaders = headers;

    return this.http.get<any>(API_URL + "login", {headers: headers}).pipe(
      map(response => {
        if (response) {
          localStorage.removeItem('currentUser');
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.currentUserSubject.next(response);
        }
        return response;
      })
    );
  }

  logOut(): Observable<any> {
    console.log("logOut");
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);

    return this.http.post(API_URL + "logout", {headers: this.userHeaders}).pipe(
      map(response => {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
      })
    );
  }

  register(user: User): Observable<any> {
    return this.http.post(API_URL + "registration", JSON.stringify(user),
      {headers: {"Content-Type": "application/json; charset=UTF-8"}});
  }

}
