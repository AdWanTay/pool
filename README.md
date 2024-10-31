<h1 align="left">Pool</h1>
<h3 align="left">Помимо обязательных требований реализованы и дополнительные:</h3>

## 1. Поиск записей по ФИО
GET getByName (/api/v0/pool/client/clientRecords)

##### Входные данные
```
    name: string
```


##### Структура ответа
```
{[
    "clientId": number,
    "name": string,
    "date": string,
    "time": string
]}
```

 
## 2. Отдельные графики для праздничных дней
Реализовано с помощью API https://holidayapi.com/. Но так как у меня бесплатная версия, и сервис предоставляет информацию только за прошлый год, то проверка работает на 2023 год.
 
## 3. Ограничение на количество записей в день на человека
В классе RecordService есть константа MAX_RECORDS_PER_CLIENT для настройки этого значения.


## 4. Возможность записи на несколько часов подряд
POST reserveConsistently (/api/v0/pool/client/reserveConsistently)

##### Структура входных данных (body) 
```
{
    "clientId": number,
    "date": string,
    "startTime": string,
    "endTime": string
}    
```

##### Структура ответа
```
[
  number,
  ...
]
```

