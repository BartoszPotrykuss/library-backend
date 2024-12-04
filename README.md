API DO ZARZĄDZANIA BIBLIOTEKĄ
Użyte technologie: Java, Spring Boot, Hibernate, Spring Data, Spring Security (token JWT), Spring Cloud, Eureka Server, API-Gateway, micrometer, zipkin, resilience4j, mySQL, JUnit, Mockito

Aplikacja jest oparta na mikroserwisach:
- discovery-server opary na netflix-eureka-server
- api-gateway, który oprócz sprawowania funkcji bramy, zabezpiecza zapytania oraz sprawdza czy w zapytaniu użyty jest prawidłowy token JWT
- identity-service, dzięki któremu można zarejestrować użytkownika oraz uzyskać token JWT
- warehouse-service, gdzie znajduje się entity książki i autora. Między nimi zachodzi relacja ManyToOne. Jest możliwość dodania książki, pobrania wszystkich książek, pobrania książki na podstawie tytułu, dodania sztuki i odjęcia sztuki.
- rental-service, gdzie można dodać wypożyczenie, zwrócić książkę. Serwis za pomocą webClient wykonuje zapytania do warehouse-service: szuka książki po tytule, dodaje jedną sztukę książki (w przypadku zwrócenia książki), odejmuje sztukę książki (w przypadku wypożyczenia) 
