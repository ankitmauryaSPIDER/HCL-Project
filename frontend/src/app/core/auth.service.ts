import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
export interface AuthResponse { token:string; userId:number; name:string; email:string; role:string; }
@Injectable({providedIn:'root'}) export class AuthService {
 private base='http://localhost:8080/api'; private state=signal<AuthResponse|null>(this.read());
 constructor(private http:HttpClient,private router:Router){}
 get current(){return this.state();} get loggedIn(){return !!this.state();} get isAdmin(){return this.state()?.role==='ADMIN';}
 login(email:string,password:string):Observable<AuthResponse>{return this.http.post<AuthResponse>(this.base+'/auth/login',{email,password}).pipe(tap(x=>this.save(x)));}
 register(name:string,email:string,password:string):Observable<AuthResponse>{return this.http.post<AuthResponse>(this.base+'/auth/register',{name,email,password}).pipe(tap(x=>this.save(x)));}
 logout(){localStorage.removeItem('portfolio_auth');this.state.set(null);this.router.navigateByUrl('/login');}
 private save(x:AuthResponse){localStorage.setItem('portfolio_auth',JSON.stringify(x));this.state.set(x);}
 private read():AuthResponse|null{try{return JSON.parse(localStorage.getItem('portfolio_auth')||'null');}catch{return null;}}
}
