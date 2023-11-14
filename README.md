# GalleryApp

Application provides the ability to take photos and observe the GPS locations, from where these photos where taken.

Stack:
- Ui: Jetpack Compose.
- Api: Retrofit.
- Datastore(local): Room.
- Di: Dagger-Hilt.
- Multithreading: Coroutines.
- Images: Coil.
- Google Maps Api for Compose.

ORM choice justification:
- Perfomance: Room has the highest perfomance compared to it's main competitors, such as GreenDAO, SQLite, Requery, ORMLite etc.
- Convenience: Despite the popular opinion, that explicit SQL-requests aren't really easy to use, I consider it quite convinient.
- Additional functionality: Room provides ability to use Flows, which is a good decision for transporting data.

# Auth screens:
<img src="https://github.com/petya3000/GalleryApp/assets/99812822/a9fe70aa-430c-47d0-9937-9bc4482dbf94" width="500" height="650"/>

#Main screens:

![image (2)](https://github.com/petya3000/GalleryApp/assets/99812822/9063ff57-6887-4b50-a5d6-9cf6510307a4)
