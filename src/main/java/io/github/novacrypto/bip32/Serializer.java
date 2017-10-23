/*
 *  BIP32 library, a Java implementation of BIP32
 *  Copyright (C) 2017 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/BIP32
 *  You can contact the authors via github issues.
 */

package io.github.novacrypto.bip32;

import static io.github.novacrypto.bip32.Sha256.sha256;

final class Serializer {

    private final Network network;
    private final boolean neutered;

    private Serializer(Builder builder) {
        network = builder.network;
        neutered = builder.neutered;
    }

    public byte[] serialize(byte[] il, byte[] ir) {
        final int version = neutered ? network.getPublicVersion() : network.getVersion();
        final byte[] privateKey = new byte[82];
        final ByteArrayWriter writer = new ByteArrayWriter(privateKey);
        writer.writeIntBigEndian(version);
        writer.writeByte((byte) 0);  //depth
        writer.writeIntBigEndian(0); //parent fingerprint, 0 for master
        writer.writeIntBigEndian(0); //child no, 0 for master
        writer.writeBytes(ir);
        if (!neutered) {
            writer.writeByte((byte) 0); //
            writer.writeBytes(il);
        } else {
            writer.writeBytes(il);
        }
        final byte[] checksum = sha256(sha256(privateKey, 0, 78));
        writer.writeBytes(checksum, 4);
        return privateKey;
    }

    static class Builder {

        private Network network;
        private boolean neutered;

        public Builder network(Network network) {
            this.network = network;
            return this;
        }

        public Builder neutered(boolean neutered) {
            this.neutered = neutered;
            return this;
        }

        public Serializer build() {
            return new Serializer(this);
        }
    }


}