/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.linea;

import org.hyperledger.besu.datatypes.TransactionType;
import org.hyperledger.besu.ethereum.GasLimitCalculator;
import org.hyperledger.besu.ethereum.mainnet.PermissionTransactionValidator;
import org.hyperledger.besu.ethereum.mainnet.TransactionValidator;
import org.hyperledger.besu.ethereum.mainnet.TransactionValidatorFactory;
import org.hyperledger.besu.ethereum.mainnet.feemarket.FeeMarket;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Suppliers;

public class LineaTransactionValidatorFactory extends TransactionValidatorFactory {
  private final int txCalldataMaxSize;

  public LineaTransactionValidatorFactory(
      final GasCalculator gasCalculator,
      final GasLimitCalculator gasLimitCalculator,
      final FeeMarket feeMarket,
      final boolean checkSignatureMalleability,
      final Optional<BigInteger> chainId,
      final Set<TransactionType> acceptedTransactionTypes,
      final int txCalldataMaxSize) {
    super(
        gasCalculator,
        gasLimitCalculator,
        feeMarket,
        checkSignatureMalleability,
        chainId,
        acceptedTransactionTypes,
        Integer.MAX_VALUE);
    this.txCalldataMaxSize = txCalldataMaxSize;
  }

  @Override
  public TransactionValidator get() {
    return Suppliers.memoize(this::createTransactionValidator).get();
  }

  private TransactionValidator createTransactionValidator() {
    final TransactionValidator baseValidator =
        new LineaTransactionValidator(
            gasCalculator,
            gasLimitCalculator,
            feeMarket,
            disallowSignatureMalleability,
            chainId,
            acceptedTransactionTypes,
            txCalldataMaxSize);
    if (permissionTransactionFilter.isPresent()) {
      return new PermissionTransactionValidator(baseValidator, permissionTransactionFilter.get());
    }
    return baseValidator;
  }
}
