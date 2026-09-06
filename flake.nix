{
  description = "Cubing app for the desktop";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ...}@inputs:
  flake-utils.lib.eachDefaultSystem (system:
  let
    pkgs = nixpkgs.legacyPackages.${system};
  in {
    devShells.default = pkgs.mkShell {
      packages = with pkgs; [
        jdk17
        mesa
        libGL
      ];
      LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath [ pkgs.mesa pkgs.libGL ];
    };
  });
}
