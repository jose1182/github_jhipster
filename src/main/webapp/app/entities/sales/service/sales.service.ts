import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISales, getSalesIdentifier } from '../sales.model';

export type EntityResponseType = HttpResponse<ISales>;
export type EntityArrayResponseType = HttpResponse<ISales[]>;

@Injectable({ providedIn: 'root' })
export class SalesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sales');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(sales: ISales): Observable<EntityResponseType> {
    return this.http.post<ISales>(this.resourceUrl, sales, { observe: 'response' });
  }

  update(sales: ISales): Observable<EntityResponseType> {
    return this.http.put<ISales>(`${this.resourceUrl}/${getSalesIdentifier(sales) as number}`, sales, { observe: 'response' });
  }

  partialUpdate(sales: ISales): Observable<EntityResponseType> {
    return this.http.patch<ISales>(`${this.resourceUrl}/${getSalesIdentifier(sales) as number}`, sales, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISales>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISales[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSalesToCollectionIfMissing(salesCollection: ISales[], ...salesToCheck: (ISales | null | undefined)[]): ISales[] {
    const sales: ISales[] = salesToCheck.filter(isPresent);
    if (sales.length > 0) {
      const salesCollectionIdentifiers = salesCollection.map(salesItem => getSalesIdentifier(salesItem)!);
      const salesToAdd = sales.filter(salesItem => {
        const salesIdentifier = getSalesIdentifier(salesItem);
        if (salesIdentifier == null || salesCollectionIdentifiers.includes(salesIdentifier)) {
          return false;
        }
        salesCollectionIdentifiers.push(salesIdentifier);
        return true;
      });
      return [...salesToAdd, ...salesCollection];
    }
    return salesCollection;
  }
}
