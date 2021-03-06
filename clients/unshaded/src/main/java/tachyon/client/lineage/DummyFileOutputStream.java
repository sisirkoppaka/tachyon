/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.client.lineage;

import java.io.IOException;

import tachyon.TachyonURI;
import tachyon.client.file.FileOutStream;
import tachyon.client.file.options.OutStreamOptions;

/**
 * A dummy file output stream that does nothing. This is used for lineage recomputation. When the
 * file is not lost on Tachyon, there's no need for the job to rewrite the file.
 */
public final class DummyFileOutputStream extends FileOutStream {

  /**
   * Constructs a new dummy file output stream.
   *
   * @param path the path of the file
   * @param options the set of options specific to this operation
   * @throws IOException if an I/O error occurs
   */
  public DummyFileOutputStream(TachyonURI path, OutStreamOptions options) throws IOException {
    super(path, options);
  }

  @Override
  public void flush() throws IOException {}

  @Override
  public void write(int b) throws IOException {}

  @Override
  public void write(byte[] b) throws IOException {}

  @Override
  public void write(byte[] b, int off, int len) throws IOException {}

  @Override
  public void close() throws IOException {}
}
