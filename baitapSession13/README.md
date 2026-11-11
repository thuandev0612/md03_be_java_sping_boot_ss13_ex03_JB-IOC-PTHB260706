# Session13 - Bài 1 đến 3: Spring Security, BCrypt và UserPrincipal

Java 21, Spring Boot, Spring Security, Lombok và PostgreSQL. Employee có id, fullName, salary; API trả danh sách 3 nhân viên được fix cứng.

## Chạy ứng dụng

1. Mở project bằng IntelliJ, chọn JDK 21 và tải lại Gradle.
2. Chạy `BaitapSession13Application`.
3. Dùng Postman thử các endpoint bên dưới, chọn Authorization → No Auth.

## Bài 1: Cơ chế mặc định đã quan sát

Trước khi thêm SecurityConfig, dependency `spring-boot-starter-security` tự bảo vệ ứng dụng: API chưa xác thực trả 401, trình duyệt nhận HTML được chuyển đến form login. Basic Auth với user và mật khẩu sinh ngẫu nhiên trong Console trả 200 cùng 3 nhân viên. Mật khẩu thay đổi sau mỗi lần chạy.

Project hiện áp dụng SecurityFilterChain của Bài 2 và đọc người dùng từ DB theo Bài 3.

## Bài 2: SecurityFilterChain và PasswordEncoder

Trong Postman chọn **GET**, Authorization → **No Auth**:

```text
GET http://localhost:8080/api/v1/employees
Accept: application/json
```

Kết quả hiện tại là **403 Forbidden**, vì endpoint yêu cầu authenticated và chưa có cơ chế đăng nhập tùy chỉnh.

### API public

```text
GET http://localhost:8080/api/v1/auth/test
```

Không cần tài khoản/mật khẩu, trả **200 OK**:

```json
{"message": "API public truy cập thành công"}
```

Endpoint test cũng nhận **POST**, trả 200 mà không cần CSRF token để quan sát cấu hình tắt CSRF.

### Các Bean đã cấu hình

- `SecurityConfig` dùng `@Configuration`.
- `PasswordEncoder` là `BCryptPasswordEncoder`. BCrypt tạo hash kèm salt ngẫu nhiên; dùng encode để tạo hash và matches để kiểm tra mật khẩu, không so sánh trực tiếp hai hash.
- `SecurityFilterChain` tắt CSRF và form login, permitAll cho `/api/v1/auth/**`, authenticated cho các request còn lại.
- HTTP Basic cũng được tắt trong cấu hình hiện tại; hướng dẫn Basic Auth của Bài 1 là hành vi trước khi thêm SecurityConfig.

Với Bài 2, truy cập `/login` không còn hiển thị form đăng nhập mặc định; request private chưa xác thực trả 403 cho cả JSON và HTML. Các API đăng ký/đăng nhập thực tế có thể được bổ sung dưới `/api/v1/auth/**` ở bài tiếp theo. Bean BCrypt đã sẵn sàng để mã hóa mật khẩu khi cần.

## Bài 3: UserDetails và UserPrincipal

Database `security_session13` đã được chuẩn bị trên máy hiện tại. Mật khẩu DB ở ngoài project tại `../.local-config/baitapSession13.properties`. Máy khác tạo database bằng `CREATE DATABASE security_session13;`, đặt DB_USERNAME/DB_PASSWORD; DB_URL cho phép đổi địa chỉ kết nối.

- Entity User ánh xạ bảng users: id tự tăng, username unique, password, role, enabled.
- role mặc định USER; enabled mặc định true.
- UserRepository có `Optional<User> findByUsername(String username)`.
- UserPrincipal implements UserDetails, chứa User và danh sách GrantedAuthority. getUsername/getPassword lấy từ User; isEnabled phản ánh enabled; ba cờ hết hạn/khóa tài khoản trả true.
- CustomUserDetailsService implements UserDetailsService, tìm User trong DB rồi tạo UserPrincipal. Không tìm thấy thì ném UsernameNotFoundException.
- Role USER được chuyển thành ROLE_USER, ADMIN thành ROLE_ADMIN. Role đã có tiền tố ROLE_ được giữ nguyên.

Luồng xác thực: AuthenticationManager → UserDetailsService → UserRepository → UserPrincipal; PasswordEncoder BCrypt kiểm tra mật khẩu nhập với hash trong DB. Khi tạo người dùng, lưu `passwordEncoder.encode(matKhauNhanTuRequest)` vào password; không mã hóa lại hash khi đọc để đăng nhập. Password được bỏ khỏi JSON khi serialize User hoặc UserPrincipal.

Bài này chuẩn bị Entity/Repository/UserDetailsService/UserPrincipal cho bước đăng nhập. API public test vẫn trả 200, API employees chưa xác thực vẫn trả 403 theo Bài 2. Spring Boot dùng UserDetailsService mới thay cho tài khoản user/mật khẩu ngẫu nhiên của Bài 1.

Kiểm tra cấu trúc dữ liệu bằng pgAdmin:

```sql
SELECT id, username, role, enabled FROM users ORDER BY id;
```

Build/cache và log kiểm tra được đặt ngoài thư mục nộp. Khi kéo thả GitHub, bỏ build/.gradle/.idea nếu IntelliJ tạo thêm.
