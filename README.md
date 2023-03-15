## hub-cep-api
API de fácil instalação para buscar CEPs no território brasileiro.

## conceito
Essa API baixa esse [csv](https://github.com/miltonhit/miltonhit/raw/main/public-assets/cep-20190602.csv) e salva na tabela 'cep.address', utilizando o banco de dados de memória h2.<br>
Há uma validação de status para saber se o serviço está pronto para ser consumido.<br>
Depois do serviço subir, é possível pesquisar, via API REST, qualquer CEP brasileiro.

## stack
-> Java 11+<br>
-> h2<br>
-> Spring Family<br>
-> Mock

## cobertura de testes
100% coberto por testes de integrações, utilizando:<br>
-> org.mock-server<br>
-> com.h2database<br>

## para compilar e testar
Baixe o projeto e execute a aplicação.<br>
**Importante:** Ao buildar e subir a API, ela pode demorar até 3 minutos para baixar todos os CEPs e inserir no h2.<br>
Até o projeto subir por completo, você vai receber o erro *503*, conforme exemplo abaixo.

## exemplos para testar
###### curl http://localhost:8080/zipcode/03358150
*200 OK*
```JSON
{
    "zipcode": "03358150",
    "street": "Rua Ituri",
    "district": "Vila Formosa",
    "state": "SP",
    "city": "São Paulo"
}
```

*503 Service Unavailable*
```JSON
{
    "timestamp": "2023-01-16T23:27:34.962+00:00",
    "status": 503,
    "error": "Service Unavailable",
    "message": "Service is in preparation. Wait a moment, please.",
    "path": "/zipcode/03358150"
}
```

###### curl http://localhost:8080/zip/9999999
*204 No-Content*
