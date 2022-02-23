import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISales, Sales } from '../sales.model';

import { SalesService } from './sales.service';

describe('Sales Service', () => {
  let service: SalesService;
  let httpMock: HttpTestingController;
  let elemDefault: ISales;
  let expectedResult: ISales | ISales[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SalesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Sales', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Sales()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sales', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sales', () => {
      const patchObject = Object.assign({}, new Sales());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sales', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Sales', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSalesToCollectionIfMissing', () => {
      it('should add a Sales to an empty array', () => {
        const sales: ISales = { id: 123 };
        expectedResult = service.addSalesToCollectionIfMissing([], sales);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sales);
      });

      it('should not add a Sales to an array that contains it', () => {
        const sales: ISales = { id: 123 };
        const salesCollection: ISales[] = [
          {
            ...sales,
          },
          { id: 456 },
        ];
        expectedResult = service.addSalesToCollectionIfMissing(salesCollection, sales);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sales to an array that doesn't contain it", () => {
        const sales: ISales = { id: 123 };
        const salesCollection: ISales[] = [{ id: 456 }];
        expectedResult = service.addSalesToCollectionIfMissing(salesCollection, sales);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sales);
      });

      it('should add only unique Sales to an array', () => {
        const salesArray: ISales[] = [{ id: 123 }, { id: 456 }, { id: 40750 }];
        const salesCollection: ISales[] = [{ id: 123 }];
        expectedResult = service.addSalesToCollectionIfMissing(salesCollection, ...salesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sales: ISales = { id: 123 };
        const sales2: ISales = { id: 456 };
        expectedResult = service.addSalesToCollectionIfMissing([], sales, sales2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sales);
        expect(expectedResult).toContain(sales2);
      });

      it('should accept null and undefined values', () => {
        const sales: ISales = { id: 123 };
        expectedResult = service.addSalesToCollectionIfMissing([], null, sales, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sales);
      });

      it('should return initial array if no Sales is added', () => {
        const salesCollection: ISales[] = [{ id: 123 }];
        expectedResult = service.addSalesToCollectionIfMissing(salesCollection, undefined, null);
        expect(expectedResult).toEqual(salesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
