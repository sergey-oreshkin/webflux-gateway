Запустить каждый модуль отдельно.   

Запросы:   
POST http://localhost:8001/gateway/login с телом   
`
{
    "phoneNumber":"54321",
    "password":"123"
}`   
вернет token

GET http://localhost:8001/gateway/passport   
с токеном в хэдере Authorization   
вернет номер паспорта