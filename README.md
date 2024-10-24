# **TradeGuard API**

_TradeGuard API, **customer** ve **asset** yönetimi, **transaction** yönetimi ve **order** işlemlerini sağlayan bir Java 
tabanlı Spring Boot uygulamasıdır. Bu uygulama H2 veritabanı, Spring Security ile güvenlik ve 
Swagger dokümantasyonu ile API endpointlerine erişim sağlar._

## İçindekiler

* Özellikler
* Teknolojiler
* Ön Gereksinimler
* Kurulum
* Çalıştırma
* Swagger
* Veritabanı
* Testler
* Prod ve Dev Ortamları
* Katkıda Bulunma

## Özellikler

* **Customer Yönetimi:**       Müşterilerin güncellenmesi (email, phone_number) ve bakiyelerinin görüntülenmesi (eğer varsa).
* **Asset Yönetimi:**          Müşteri varlıklarının gösterilmesi (yetkisine göre).
* **Transaction Yönetimi:**    Para yatırma ve çekme işlemlerinin yapılması (customer lar tarafından).
* **Order Yönetimi:**          Satın alma ve satış emirlerinin oluşturulması, iptal edilmesi ve eşleştirilmesi.
* **Swagger Desteği:**         API dokümantasyonu için Swagger UI.
* **Spring Security:**         Role dayalı güvenlik yapılandırması.

## Teknolojiler

Projede kullanılan başlıca teknolojiler şunlardır:

1. Java 17
2. Spring Boot 3.x
3. H2 Veritabanı
4. Spring Security
5. Swagger UI
6. Maven

## Ön Gereksinimler

Bu projeyi çalıştırmak için aşağıdaki gereksinimlere sahip olmanız gerekmektedir:

1. Java 17
2. Maven 3.6+
3. Docker (isteğe bağlı)
4. H2 veritabanı (entegre)
5. Swagger (dokümantasyon için)

## Kurulum

1. Bu projeyi klonlayın:
git clone https://github.com/omeraltan/project.git
cd project

2. Maven bağımlılıklarını yükleyin:
   mvn clean install
3. Profil ayarlarına göre dev veya prod ortamlarını yapılandırın: 
* Geliştirme ortamı için application-dev.properties
* Üretim ortamı için application-prod.properties

## Çalıştırma
Projeyi çalıştırmak için aşağıdaki adımları izleyin:

## Geliştirme Ortamı:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

## Prod Ortamı:
mvn spring-boot:run -Dspring-boot.run.profiles=prod

Veya JAR dosyasını kullanarak çalıştırabilirsiniz:
java -jar target/tradeguard-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

## Swagger
Geliştirme ortamında Swagger UI'ye şu adresten erişebilirsiniz:
* http://localhost:9090/swagger-ui/index.html

Üretim ortamında, Swagger’a yalnızca USER ve ADMIN rollerine sahip kullanıcılar erişebilir.

## Veritabanı
Bu proje H2 bellek içi veritabanı kullanır. Geliştirme sırasında H2 Console'a şu adresten erişebilirsiniz (ADMIN):
* http://localhost:9090/h2-console (dev)

H2 Console Ayarları:
* JDBC URL: jdbc:h2:mem:testdb
* Kullanıcı Adı: sa
* Şifre: (boş)

## Testler
Projede yer alan testleri çalıştırmak için şu komutu kullanabilirsiniz:
mvn test

Testler, uygulamanın güvenliğini, iş mantığını ve veri bütünlüğünü doğrulamak için kapsamlı şekilde yazılmıştır. 
Ayrıca JaCoCo ile test kapsamı raporu alabilirsiniz.


## Kullanım Senaryosu
* Örnek Kullanıcılar ve Varlıklar
Uygulama başlatıldığında, data.sql den 
**users**, **customer** ve **asset** tablolarına müşteri bilgileri, varlıkları, kullanıcı adı ve şifreleri yüklenir.
Uygulamaya giriş yapabilmek için önünüze login ekranı gelecek
**USER** kullanıcıları veya **ADMIN** ile giriş yapabilirsiniz.
Örneğin: kullanıcı adı: omer şifre: omer123 (user) veya kullanıcı adı: admin şifre: admin123 (admin)

Giriş yapıldığında önünüze swagger ekranı gelecektir.
user rolündeki personel kendi rolüne göre admin kendi rolüne göre yetkilendirilmiştir. 
swagger yetkiler dahilinde kullanılabilir.

### Swagger Endpointleri ve Roller

| **Endpoint**                                 | **Açıklama**                                           | **Kullanıcı**                                | **Admin** |
|----------------------------------------------|--------------------------------------------------------|----------------------------------------------|-----------|
| `/api/v1/customers/{customerId}`             | Belirli bir müşterinin bilgilerini getirir.            | Sadece kendi varlıklarına erişim sağlar.     | Evet      |
| `/api/v1/customers/all`                      | Tüm müşterilerin listesini döner.                      | Hayır                                        | Evet      |
| `/api/v1/assets/{customerId}/{assetName}`    | Belirli bir müşterinin belirli bir varlığını getirir.  | Sadece kendi varlıklarına erişim sağlar.     | Evet      |
| `/api/v1/assets/all`                         | Tüm varlıkların listesini döner.                       | Hayır                                        | Evet      |
| `/api/v1/orders/list`                        | Müşterinin sipariş geçmişini getirir.                  | Sadece kendi varlıklarına erişim sağlar.     | Evet      |
| `/api/v1/orders/{orderId}`                   | Belirli bir siparişin detaylarını getirir.             | Evet                                         | Evet      |
| `/api/v1/orders/{orderId}/match`             | Belirli bir siparişin eşleşme işlemini başlatır.       | Hayır                                        | Evet      |
| `/api/v1/orders`                             | Yeni bir sipariş oluşturur (alım/satım).               | Evet                                         | Hayır     |
| `/api/v1/transactions/customer/{customerId}` | Belirli bir müşterinin işlem geçmişini getirir.        | kendi işlemlerine erişim sağlar.             | Evet      |
| `/api/v1/transactions/all`                   | Tüm işlemlerin listesini döner.                        | Hayır                                        | Evet      |
| `/api/v1/transactions/deposit`               | Para yatırma işlemi başlatır.                          | Evet                                         | Hayır     |
| `/api/v1/transactions/withdraw`              | Para çekme işlemi başlatır.                            | Evet                                         | Hayır     |
| `/swagger-ui/index.html`                     | Swagger arayüzüne erişim sağlar.                       | Evet                                         | Evet      |
| `/h2-console/**`                             | H2 veritabanı konsoluna erişim sağlar.                 | Hayır                                        | Evet      |


## Order İşlemi İçin Örnek Veriler

* customerId = 2 (omer kullanıcısı)
  * customerId = 3 (ebru kullanıcısı)
      {
      "customerId": 2,
      "assetName": "Tesla Inc. Stock",
      "orderSide": "SELL",
      "size": 5,
      "price": 300,
      "status": "PENDING",
      "createDate": "2024-10-24T08:55:18.296396"
      }
    
    {
    "customerId": 2,
    "assetName": "Amazon Stock",
    "orderSide": "BUY",
    "size": 5,
    "price": 400,
    "status": "PENDING",
    "createDate": "2024-10-24T09:11:10.296396"
    }
    
    {
    "customerId": 2,
    "assetName": "Litecoin",
    "orderSide": "BUY",
    "size": 10,
    "price": 400,
    "status": "PENDING",
    "createDate": "2024-10-24T09:11:10.296396"
    }
    
    {
    "customerId": 3,
    "assetName": "Tesla Inc. Stock",
    "orderSide": "BUY",
    "size": 5,
    "price": 300,
    "status": "PENDING",
    "createDate": "2024-10-24T09:00:18.296396"
    }

