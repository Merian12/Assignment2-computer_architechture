import glob
import struct
import subprocess
import sys


tests_folder = sys.argv[1] if len(sys.argv) > 1 else "tests"

# Compile the simulator
subprocess.run(["javac", "IsaSim.java"], check=True)

passed = 0
# .bin files directly in the folder, or one level down (e.g. tests/task1/*.bin)
bin_files = sorted(glob.glob(tests_folder + "/*.bin") + glob.glob(tests_folder + "/*/*.bin"))

for bin_file in bin_files:
    res_file = bin_file.replace(".bin", ".res")
    out_file = bin_file + ".out"  # IsaSim writes <input>.out

    # Run the simulator (stop it after 10 seconds in case of an infinite loop)
    try:
        subprocess.run(["java", "IsaSim", bin_file], timeout=30, capture_output=True)
    except subprocess.TimeoutExpired:
        print("FAIL", bin_file, "(timed out)")
        continue

    # Read both files as 32 little-endian integers (x0..x31)
    try:
        got = struct.unpack("<32i", open(out_file, "rb").read())
    except FileNotFoundError:
        print("FAIL", bin_file, "(no .out file, simulator crashed?)")
        continue
    expected = struct.unpack("<32i", open(res_file, "rb").read())

    if got == expected:
        print("PASS", bin_file)
        passed += 1
    else:
        print("FAIL", bin_file)
        for i in range(32):
            if got[i] != expected[i]:
                print(f"   x{i}: expected {expected[i]}, got {got[i]}")

print(f"\n{passed}/{len(bin_files)} tests passed")
