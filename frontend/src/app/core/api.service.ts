import { Injectable } from '@angular/core'; import { HttpClient } from '@angular/common/http';
@Injectable({providedIn:'root'}) export class ApiService {base='http://localhost:8080/api';constructor(private http:HttpClient){}
 dashboard(){return this.http.get<any>(this.base+'/portfolio/dashboard');} stocks(q=''){return this.http.get<any[]>(this.base+'/stocks',q?{params:{q}}:{});} stock(id:number){return this.http.get<any>(this.base+'/stocks/'+id);}
 holdings(){return this.http.get<any[]>(this.base+'/portfolio/holdings');} transactions(){return this.http.get<any[]>(this.base+'/transactions');} watchlist(){return this.http.get<any[]>(this.base+'/portfolio/watchlist');}
 addWatch(id:number){return this.http.post(this.base+'/portfolio/watchlist/'+id,{});} removeWatch(id:number){return this.http.delete(this.base+'/portfolio/watchlist/'+id);}
 trade(symbol:string,type:string,quantity:number){return this.http.post(this.base+'/trades',{symbol,type,quantity});}
 me(){return this.http.get<any>(this.base+'/users/me');} adminUsers(){return this.http.get<any[]>(this.base+'/admin/users');} adminTrades(){return this.http.get<any[]>(this.base+'/admin/trades');}
 adminTransactions(){return this.http.get<any[]>(this.base+'/admin/transactions');}
 createStock(s:any){return this.http.post(this.base+'/admin/stocks',s);} updateStock(id:number,s:any){return this.http.put(this.base+'/admin/stocks/'+id,s);} deleteStock(id:number){return this.http.delete(this.base+'/admin/stocks/'+id);}
}
