# account-service
Implementa as funcionalidades de criação de conta e transações do débito e crédito.

## Exemplos de JSON gerado em cada transação

### Débito
```json
{
   "number":"1001",
   "branch":1,
   "bank":"121",
   "balance":5048.00,
   "taxId":"61247909042",
   "version":"1",
   "eventType":"DEBIT",
   "receiptNumber":"28fcceef-6926-4f35-b429-9dec978dcdf9",
   "amount":11.00
}
```

### Crédito
```json
{
   "number":"1002",
   "branch":1,
   "bank":"121",
   "balance":62.00,
   "taxId":"69548925052",
   "version":"1",
   "eventType":"CREDIT",
   "receiptNumber":"28fcceef-6926-4f35-b429-9dec978dcdf9",
   "amount":11.00
}
```