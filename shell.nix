let
  pkgs = import <nixpkgs> { config = {}; overlays = []; };
in
pkgs.mkShellNoCC {
  packages =  [
    # pkgs.clang
  ];
  LLVM_BIN = "${pkgs.clang}/bin";
}
