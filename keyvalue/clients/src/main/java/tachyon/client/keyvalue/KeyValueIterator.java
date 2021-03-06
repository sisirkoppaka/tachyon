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

package tachyon.client.keyvalue;

import java.io.IOException;

import tachyon.annotation.PublicApi;
import tachyon.exception.TachyonException;

/**
 * Iterator to iterate over key-value pairs in {@link KeyValueStores} or its partitions.
 */
@PublicApi
public interface KeyValueIterator {
  /**
   * @return true if the iterator has more key-value pairs, otherwise false
   */
  boolean hasNext();

  /**
   * @return the next key-value pair in the iteration
   * @throws IOException if a non-Tachyon exception occurs
   * @throws TachyonException if an unexpected Tachyon exception is thrown
   * @throws java.util.NoSuchElementException if there are no more pairs
   */
  KeyValuePair next() throws IOException, TachyonException;
}
