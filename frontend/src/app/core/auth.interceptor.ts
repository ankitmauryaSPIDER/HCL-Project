import { HttpInterceptorFn } from '@angular/common/http';
export const authInterceptor:HttpInterceptorFn=(req,next)=>{const raw=localStorage.getItem('portfolio_auth');if(!raw)return next(req);try{const a=JSON.parse(raw);return next(req.clone({setHeaders:{Authorization:`Bearer ${a.token}`}}));}catch{return next(req);}};
