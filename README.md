<div align="center">

<img src="assets/cuber-logo.svg" width="100" alt="">
<h1>
  Cuber
</h1>

**Experimental [Cuber](https://github.com/jvqtil/Cuber) fork** for desktop. Windows, MacOS and Linux supported

[**Usage**](#usage)
&nbsp;•&nbsp;
[**Features**](#features)
&nbsp;•&nbsp;
[**Contributing**](#contributing)

<a href="https://github.com/jvqtil/CuberDesktop/releases/latest">
  <img src="https://img.shields.io/badge/Download-FF8A3D?style=for-the-badge&logo=github&logoColor=white" alt="Download">
</a>

<br>
<br>

<table>
  <tr>
    <td><img src="assets/screenshots/cuberdesktop_timer_screen.png" width="400" alt="Timer"></td>
    <td><img src="assets/screenshots/cuberdesktop_solves_screen.png" width="400" alt="Statistics and solve history"></td>
    <td><img src="assets/screenshots/cuberdesktop_solve_details_screen.png" width="400" alt="Solve details"></td>
  </tr>
</table>

</div>

## Usage
Click the timer to start/stop. **Long press** to reset.
Use escape key to go back from any menu. When pressed on timer screen - navigates to solves

## Features

- Fast and simple **speedcubing** timer
- WCA-style **3×3 scrambles**
- **Visual scramble preview**
- Solves history
- **Statistics**
- **`+2`** and **`DNF`** penalties
- Comments for individual solves
- Local-only storage

## Contributing

### Building
Requires JDK 17.

Run locally:
```bash
./gradlew :desktopApp:run
```

Build native packages on the target OS:
```bash
./gradlew :desktopApp:packageDmg     # macOS
./gradlew :desktopApp:packageExe     # Windows
./gradlew :desktopApp:packageDeb     # Debian / Ubuntu
```

## Thanks to

* [tnoodle-lib](https://github.com/thewca/tnoodle-lib) — Scramble generation library.
