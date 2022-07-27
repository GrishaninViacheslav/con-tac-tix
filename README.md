<b>
  <p>
    Скачать: 
    <a href="https://github.com/GrishaninVyacheslav/reddit-pagging/releases/download/pre-release/reddit_pagging.apk">reddit_pagging.apk</a>
  <p/>
  <p>
    Стек технологий: Retrofit, Kotlin Coroutines, Room, Fragments.
  <p/>
</b>
<p>
Демонстрация работы приложения: https://youtu.be/fvXm39dM1T4
<p/>
Приложение, демонстрирующее реализацию пагинации популярных
постов reddit. Состоит из одного экрана с бесконечным списком постов
с Reddit. Загрузка списка осуществляется не постранично, а
динамически: новые посты подгружаются, пока пользователь
прокручивает список. По мере прокручивания списка, просмотренные
посты кэшируются с помощью Room. Посты из кэша отображаются и в
случае отсутствия интернета. 
<p align="center">
  <img src="preview_a.jpg" width="197" height="426">
  <img src="preview_b.jpg" width="197" height="426">
  <img src="preview_c.jpg" width="197" height="426">
</p>
